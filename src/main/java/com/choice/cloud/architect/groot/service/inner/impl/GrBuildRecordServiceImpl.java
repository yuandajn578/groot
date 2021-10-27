package com.choice.cloud.architect.groot.service.inner.impl;

import com.aliyun.openservices.ons.api.SendResult;
import com.choice.cloud.architect.groot.dao.GrBuildRecordMapper;
import com.choice.cloud.architect.groot.dao.GrChangeLifeCircleMapper;
import com.choice.cloud.architect.groot.dao.GrChangeMapper;
import com.choice.cloud.architect.groot.dao.GrMemberMapper;
import com.choice.cloud.architect.groot.dao.GrPublishMapper;
import com.choice.cloud.architect.groot.dto.GetLinkedMemberNameByChangeIdResultDTO;
import com.choice.cloud.architect.groot.dto.ListBuildRecordDTO;
import com.choice.cloud.architect.groot.enums.*;
import com.choice.cloud.architect.groot.model.GrBuildRecord;
import com.choice.cloud.architect.groot.model.GrChange;
import com.choice.cloud.architect.groot.model.GrChangeLifeCircle;
import com.choice.cloud.architect.groot.model.GrMember;
import com.choice.cloud.architect.groot.model.GrPublish;
import com.choice.cloud.architect.groot.mq.publish.MqSendNoticeService;
import com.choice.cloud.architect.groot.mq.publish.PublishSendNoticeDTO;
import com.choice.cloud.architect.groot.option.GlobalConst;
import com.choice.cloud.architect.groot.option.WebPage;
import com.choice.cloud.architect.groot.process.param.WebhookText;
import com.choice.cloud.architect.groot.process.param.WebhookTextParam;
import com.choice.cloud.architect.groot.process.service.MessageCenterProcessFeignClient;
import com.choice.cloud.architect.groot.remote.AppListResponse;
import com.choice.cloud.architect.groot.remote.DomainArchitectureDTO;
import com.choice.cloud.architect.groot.remote.jenkins.JenkinsService;
import com.choice.cloud.architect.groot.remote.milkyway.ApplicationDTO;
import com.choice.cloud.architect.groot.remote.milkyway.ApplicationParamAppCode;
import com.choice.cloud.architect.groot.remote.milkyway.DomainArchitectureParamId;
import com.choice.cloud.architect.groot.remote.milkyway.MilkywayClient;
import com.choice.cloud.architect.groot.remote.milkyway.MilkywayService;
import com.choice.cloud.architect.groot.request.ListBuildRecordRequest;
import com.choice.cloud.architect.groot.response.ResponseData;
import com.choice.cloud.architect.groot.service.inner.ContainerManageService;
import com.choice.cloud.architect.groot.service.inner.GrBuildRecordService;
import com.choice.cloud.architect.groot.service.inner.GrMemberService;
import com.choice.cloud.architect.groot.support.PollingHandler;
import com.choice.cloud.architect.groot.util.CommonUtil;
import com.choice.driver.jwt.entity.AuthUser;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/6 11:30
 */
@Service
@Slf4j
public class GrBuildRecordServiceImpl extends BaseService implements GrBuildRecordService {

    @Value("${client.perpetual.token}")
    private String token;

    @Value("${publish.robot.dingtalk.accesstoken}")
    private String accessToken;

    @Autowired
    private GrBuildRecordMapper grBuildRecordMapper;

    @Autowired
    private GrPublishMapper grPublishMapper;

    @Autowired
    private GrChangeLifeCircleMapper grChangeLifeCircleMapper;

    @Autowired
    private JenkinsService jenkinsService;

    @Autowired
    private MilkywayService milkywayService;

    @Autowired
    private GrChangeMapper grChangeMapper;

    @Autowired
    private MessageCenterProcessFeignClient messageCenterProcessFeignClient;

    @Autowired
    private ContainerManageService containerManageService;
    
    @Autowired
    private GrMemberService grMemberService;

    @Autowired
    private MqSendNoticeService mqSendNoticeService;

    @Autowired
    private MilkywayClient milkywayClient;
    
    @Override
    public WebPage<List<ListBuildRecordDTO>> pageList(ListBuildRecordRequest recordRequest) {
        Integer pageNum = recordRequest.getPageNum();
        Integer pageSize = recordRequest.getPageSize();
        List<ListBuildRecordDTO> buildRecordDTOList = Lists.newArrayList();
        PageHelper.startPage(pageNum, pageSize);
        List<GrBuildRecord> grBuildRecordList = grBuildRecordMapper.listWithPage(GlobalConst.NOT_DELETE, recordRequest.getPublishOid());

        if (CollectionUtils.isEmpty(grBuildRecordList)) {
            return new WebPage<>(pageNum, pageSize, 0, null);
        }

        for (GrBuildRecord grBuildRecord : grBuildRecordList) {
            ListBuildRecordDTO buildRecordDTO = new ListBuildRecordDTO();
            BeanUtils.copyProperties(grBuildRecord, buildRecordDTO);
            buildRecordDTOList.add(buildRecordDTO);
        }

        PageInfo<GrBuildRecord> info = new PageInfo <>(grBuildRecordList);
        return new WebPage<>(pageNum, pageSize, info.getTotal(), buildRecordDTOList);
    }

    @Override
    public GrBuildRecord getLastRecordByPublishOid(String publishOid) {
        return grBuildRecordMapper.getLastRecordByPublishOid(publishOid);
    }

    @Override
    public boolean existNormalBuilding(String appCode) {
        List<GrBuildRecord> grBuildRecordList = grBuildRecordMapper.getNormalBuildingLastRecord(
                appCode, BuildResultEnum.BUILDING.getValue()
        );

        for (GrBuildRecord grBuildRecord: grBuildRecordList) {
            String jobName = grBuildRecord.getJobName();
            int num = grBuildRecord.getBuildNum();
            GrPublish grPublish = grPublishMapper.selectById(grBuildRecord.getPublishOid());
            long duration = 0;
            try {
                Build build = jenkinsService.getBuild(jobName, num);
                BuildResult buildResult = build.details().getResult();
                duration = build.details().getDuration();
                if (buildResult == null) {
                    log.warn("未获取构建详情 jobName={}， buildNum={}", jobName, num);
                    grBuildRecordMapper.updateResult(jobName, num, BuildResult.UNKNOWN.name(), duration);
                    grPublish.setPublishStatus(PublishStatusEnum.WAITING.value());
                    grPublishMapper.updateDynamic(grPublish);
                    return false;
                }
                if (BuildResult.BUILDING.equals(buildResult) || BuildResult.REBUILDING.equals(buildResult)){
                    return true;
                } else if (BuildResult.SUCCESS.equals(buildResult)){
                    grBuildRecordMapper.updateResult(jobName, num, BuildResult.SUCCESS.name(), duration);
                    grPublish.setPublishStatus(PublishStatusEnum.SUCCESS.value());
                    grPublishMapper.updateDynamic(grPublish);
                    log.info("补偿构建结果： jobName={}， buildNum={}", jobName, num);
                } else {
                    updateRecordResult(jobName, num);
                }
            } catch (Exception e) {
                log.warn("未获取构建详情 jobName={}， buildNum={}", jobName, num);
                grBuildRecordMapper.updateResult(jobName, num, BuildResult.UNKNOWN.name(), duration);
                grPublish.setPublishStatus(PublishStatusEnum.FAILURE.value());
                grPublishMapper.updateDynamic(grPublish);
            }
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBuildRecord(String jobName, String publishOid, String appCode, String result, int buildNum, String version, AuthUser authUser) {
        // 不查旧的只要新的
        GrBuildRecord grBuildRecord = new GrBuildRecord();
        initSaveModel(grBuildRecord, authUser);
        grBuildRecord.setBuildNum(buildNum);
        grBuildRecord.setAppCode(appCode);
        grBuildRecord.setPublishOid(publishOid);
        grBuildRecord.setResult(result);
        grBuildRecord.setJobName(jobName);
        grBuildRecord.setVersion(version);
        grBuildRecordMapper.insert(grBuildRecord);
        log.info("Groot 构建记录创建成功：{}", grBuildRecord.toString());
    }

    @Override
    @Async("alarmNoticeTaskExecutor")
    public void updateRecordResult(String jobName, int buildNum) {
        Build build;
        BuildResult buildResult = BuildResult.UNKNOWN;
        try {
            buildResult = new PollingHandler<BuildResult>().process(
                    5, 5, () -> {
                        try {
                            return jenkinsService.getBuild(jobName, buildNum).details().getResult();
                        } catch (IOException e) {
                            return null;
                        }
                    }
            );
            // 需要再获取一次得到build最终状态
            build = jenkinsService.getBuild(jobName, buildNum);
            grBuildRecordMapper.updateResult(jobName, buildNum, buildResult.name(), build.details().getDuration());
            log.info("更新构建记录状态，jobName={}, buildNum={}, result={}", jobName, buildNum, buildResult.name());
        } catch (Exception e) {
            log.error("jenkins 构建回调接口报错 (-.-#)", e);
        }
        GrBuildRecord grBuildRecord = grBuildRecordMapper.queryByBuild(jobName, buildNum);
        GrPublish grPublish = grPublishMapper.selectById(grBuildRecord.getPublishOid());
        //更新发布状态

        if (buildResult.name().equalsIgnoreCase(BuildResult.SUCCESS.name())) {
            grPublish.setPublishStatus(PublishStatusEnum.SUCCESS.value());
            String deploymentRevision = containerManageService
                    .getDeploymentRevision(grPublish.getEnvType(), grPublish.getEnvCode(), grPublish.getAppCode());
            grPublish.setDeploymentRevision(deploymentRevision);
        } else {
            grPublish.setPublishStatus(PublishStatusEnum.FAILURE.value());
            grPublish.setDeploymentRevision("0");
        }

        grPublish.setTestStatus(TestStatusEnum.TESTING.value());


        grPublishMapper.updateDynamic(grPublish);
        log.info("更新发布单状态, publish={}, status={}", grPublish.getOid(), grPublish.getPublishStatus());
        GrChangeLifeCircle grChangeLifeCircle = new GrChangeLifeCircle();
        grChangeLifeCircle.setChangeId(grPublish.getChangeId());
        if (grPublish.getEnvType().equalsIgnoreCase(EnvTypeEnum.pro.value())) {
            if (grPublish.getEnvCode().equalsIgnoreCase(EnvTypeEnum.pre.value())) {
                grChangeLifeCircle.setEnvType(EnvTypeEnum.pre.value());
            } else {
                grChangeLifeCircle.setEnvType(EnvTypeEnum.pro.value());
            }
        } else {
            grChangeLifeCircle.setEnvType(EnvTypeEnum.test.value());
        }
        grChangeLifeCircle.setEnvCode(grPublish.getEnvCode());
        grChangeLifeCircle.setEnvName(grPublish.getEnvName());
        grChangeLifeCircle.setAppCode(grPublish.getAppCode());
        grChangeLifeCircle.setChangeName(grPublish.getChangeName());
        grChangeLifeCircle.setOperateTime(LocalDateTime.now());
        grChangeLifeCircle.setOperateResult(
                buildResult.name().equalsIgnoreCase(BuildResult.SUCCESS.name())?
                        OperateResultEnum.SUCCESS.value():
                        OperateResultEnum.FAILURE.value()
        );
        grChangeLifeCircle.setOperator(grBuildRecord.getCreateUser());
        grChangeLifeCircle.setProject(grPublish.getDomainArchitectureName());
        grChangeLifeCircle.setRelOrderType(RelOrderTypeEnum.ORDER_PUBLISH.value());
        grChangeLifeCircle.setRelOrderId(grPublish.getOid());
        grChangeLifeCircle.setOperateType(OperateTypeEnum.DEPLOY.value());
        grChangeLifeCircle.setOid(CommonUtil.getUUIdUseMongo());
        grChangeLifeCircle.setDeleteFlag(GlobalConst.NOT_DELETE);
        grChangeLifeCircle.setCreateTime(LocalDateTime.now());
        grChangeLifeCircleMapper.insert(grChangeLifeCircle);
        log.info("更新生命周期：{}", grChangeLifeCircle.toString());
        if (EnvTypeEnum.test.value().equalsIgnoreCase(grPublish.getEnvType())
                && !"stable".equalsIgnoreCase(grPublish.getEnvCode())) {
            return;
        }
        sendDingTalkMessage(grPublish, grBuildRecord, buildResult);

        // 根据变更单id查询关联的人员信息
        GetLinkedMemberNameByChangeIdResultDTO memberNameResultDTO = grMemberService.getLinkedMemberNameByChangeId(grPublish.getChangeId());

        // 只有生产环境才会发送消息
        if (EnvTypeEnum.pro.value().equalsIgnoreCase(grPublish.getEnvType()) && StringUtils.isNotBlank(accessToken)) {
            // 发送群机器人消息
            this.sendRobotMessage(grPublish, grBuildRecord, buildResult, memberNameResultDTO);
        }

        if (buildResult.name().equalsIgnoreCase(BuildResult.SUCCESS.name()) &&
            EnvTypeEnum.pro.value().equalsIgnoreCase(grPublish.getEnvType()) &&
            "stable".equalsIgnoreCase(grPublish.getEnvCode())) {
            // 更改变更单状态
            GrChange grChange = new GrChange();
            grChange.setOid(grPublish.getChangeId());
            grChange.setStatus(ChangeEnum.RELEASED.getCode());
            grChange.setUpdateUser("Groot");
            grChange.setUpdateTime(LocalDateTime.now());
            grChangeMapper.updateChangeStatus(grChange);

            // 发送延迟消息，发布成功未验收，发工作通知功能
            // 验收完成请点击验收通过, 发布完成未点击验收通过，3h通知应用owner+应用发布人，6h通知架构域，10h冲哥
            PublishSendNoticeDTO message = this.assembleMessage(grPublish);
            log.info("[发布回调]发送参数，message：{}", JSON.toJSONString(message));
            SendResult sendResult = mqSendNoticeService.sendNoticeMsg(JSON.toJSONString(message), PublishSendNoticePhaseEnum.ONE_PHASE.getCode());
            log.info("[发布回调]发送结果，sendResult：{}", JSON.toJSONString(sendResult));
        }
    }

    public PublishSendNoticeDTO assembleMessage(GrPublish grPublish) {
        PublishSendNoticeDTO message = new PublishSendNoticeDTO();
        message.setChangeId(grPublish.getChangeId());
        message.setPhaseCode(PublishSendNoticePhaseEnum.ONE_PHASE.getCode());
        message.setPublishUserId(grPublish.getOperatorId());

        // 根据应用code查询应用信息
        ApplicationParamAppCode appParam = new ApplicationParamAppCode();
        appParam.setAppCode(grPublish.getAppCode());
        ResponseData<ApplicationDTO> appResponse = milkywayClient.getApplicationInfoByAppCode(token, appParam);
        log.info("获取到的应用信息：{}", JSON.toJSONString(appResponse));
        ApplicationDTO appDTO = new ApplicationDTO();
        if (appResponse.isSuccess() && null != appResponse.getData()) {
            appDTO = appResponse.getData();
        }
        message.setOwnerId(appDTO.getOwnerId());
        // 根据架构域id查询架构域信息
        DomainArchitectureParamId domainParam = new DomainArchitectureParamId();
        domainParam.setOid(appDTO.getDomainArchitectureId());
        ResponseData<DomainArchitectureDTO> domainArchitectureResponse = milkywayClient.getDomainArchitectureInfoByOid(token, domainParam);
        log.info("获取到的架构域信息：{}", JSON.toJSONString(domainArchitectureResponse));
        DomainArchitectureDTO domainArchitectureDTO = new DomainArchitectureDTO();
        if (domainArchitectureResponse.isSuccess() && null != domainArchitectureResponse.getData()) {
            domainArchitectureDTO = domainArchitectureResponse.getData();
        }
        message.setDomainOwnerId(domainArchitectureDTO.getOwnerId());
        return message;
    }

    /**
     * 发送机器人消息
     * @param grPublish
     * @param grBuildRecord
     * @param buildResult
     */
    private void sendRobotMessage(GrPublish grPublish, GrBuildRecord grBuildRecord, BuildResult buildResult,
        GetLinkedMemberNameByChangeIdResultDTO memberNameResultDTO) {
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype("markdown");
        OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
        markdown.setTitle("发布完成应用");

        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append("## 发布完成应用：").append("  \n  ");
        contentBuilder.append("**应用名称**：").append(grPublish.getAppCode()).append("  \n  ");
        contentBuilder.append("**环境类型**：").append(grPublish.getEnvType()).append("  \n  ");
        contentBuilder.append("**服务分组**：").append(grPublish.getEnvCode()).append("  \n  ");
        contentBuilder.append("**部署结果**：").append(buildResult.name()).append("  \n  ");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        contentBuilder.append("**开始时间**：").append(grPublish.getReleaseTime().format(dateTimeFormatter)).append("  \n  ");
        contentBuilder.append("**构建时长**：").append(grBuildRecord.getDuration() / 1000d).append(" s").append("  \n  ");
        contentBuilder.append("**结束时间**：").append(LocalDateTime.now().format(dateTimeFormatter)).append("  \n  ");
        contentBuilder.append("**发布人员**：").append(grBuildRecord.getCreateUser()).append("  \n  ");
        if (CollectionUtils.isNotEmpty(memberNameResultDTO.getDevelopmentMembers())) {
            contentBuilder.append("**开发人员**：").append(Joiner.on(",").join(memberNameResultDTO.getDevelopmentMembers().iterator())).append("  \n  ");
        }
        if (CollectionUtils.isNotEmpty(memberNameResultDTO.getTestMembers())) {
            contentBuilder.append("**测试人员**：").append(Joiner.on(",").join(memberNameResultDTO.getTestMembers().iterator())).append("  \n  ");
        }
        if (CollectionUtils.isNotEmpty(memberNameResultDTO.getCrMembers())) {
            contentBuilder.append("**CR人员**：").append(Joiner.on(",").join(memberNameResultDTO.getCrMembers().iterator())).append("  \n  ");
        }
        if (EnvTypeEnum.pro.value().equals(grPublish.getEnvType())) {
            contentBuilder.append("**注意事项**：").append("如果验证通过，请去变更中点击验收通过，系统会自动将此分支代码合并到master").append("  \n  ");
        }
        log.info("发布完成，钉钉机器人通知内容：{}", contentBuilder.toString());

        markdown.setText(contentBuilder.toString());
        request.setMarkdown(markdown);
        ResponseData<String> responseData = messageCenterProcessFeignClient.sendWebhookMessageNew(accessToken, request);
        if (responseData.isSuccess()) {
            log.info("发布完成，钉钉机器人通知调用结果： {}", responseData.getData());
        } else {
            log.error("发布完成，钉钉机器人通知调用失败： {}", responseData.getData());
        }
    }

    private void sendDingTalkMessage(GrPublish grPublish, GrBuildRecord grBuildRecord, BuildResult buildResult) {
        AppListResponse appListResponse = milkywayService.getAppInfo(grPublish.getAppCode());
        if (appListResponse != null && StringUtils.isNotBlank(appListResponse.getOwnerId())) {
            OapiMessageCorpconversationAsyncsendV2Request request = new OapiMessageCorpconversationAsyncsendV2Request();
            String userIdList = appListResponse.getOwnerId();
            if (StringUtils.isNotBlank(grPublish.getOperatorId()) && !grPublish.getOperatorId().equals(appListResponse.getOwnerId())) {
                userIdList = userIdList + "," + grPublish.getOperatorId();
            }
            request.setUseridList(userIdList);
            OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
            msg.setMsgtype("markdown");
            msg.setMarkdown(new OapiMessageCorpconversationAsyncsendV2Request.Markdown());
            StringBuilder contentBuilder = new StringBuilder();
            contentBuilder.append("**应用名称**：").append(grPublish.getAppCode()).append("  \n  ");
            contentBuilder.append("**环境类型**：").append(grPublish.getEnvType()).append("  \n  ");
            contentBuilder.append("**服务分组**：").append(grPublish.getEnvCode()).append("  \n  ");
            contentBuilder.append("**部署结果**：").append(buildResult.name()).append("  \n  ");
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            contentBuilder.append("**开始时间**：").append(grPublish.getReleaseTime().format(dateTimeFormatter)).append("  \n  ");
            contentBuilder.append("**构建时长**：").append(grBuildRecord.getDuration() / 1000d).append(" s").append("  \n  ");
            contentBuilder.append("**发布人员**：").append(grBuildRecord.getCreateUser()).append("  \n  ");
            if (EnvTypeEnum.pro.value().equals(grPublish.getEnvType())) {
                contentBuilder.append("**注意事项**：").append("如果验证通过，请去变更中点击验收通过，系统会自动将此分支代码合并到master").append("  \n  ");
            }
            msg.getMarkdown().setText(contentBuilder.toString());
            msg.getMarkdown().setTitle("Groot发布通知");
            request.setMsg(msg);
            log.info("钉钉工作通知内容：{}", contentBuilder.toString());
            ResponseData<String> res = messageCenterProcessFeignClient.sendCorporationNotice(request);
            log.info("钉钉工作通知调用结果： {}", res.getMsg());
        }
    }
}
