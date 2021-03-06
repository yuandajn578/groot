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
                    log.warn("????????????????????? jobName={}??? buildNum={}", jobName, num);
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
                    log.info("????????????????????? jobName={}??? buildNum={}", jobName, num);
                } else {
                    updateRecordResult(jobName, num);
                }
            } catch (Exception e) {
                log.warn("????????????????????? jobName={}??? buildNum={}", jobName, num);
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
        // ????????????????????????
        GrBuildRecord grBuildRecord = new GrBuildRecord();
        initSaveModel(grBuildRecord, authUser);
        grBuildRecord.setBuildNum(buildNum);
        grBuildRecord.setAppCode(appCode);
        grBuildRecord.setPublishOid(publishOid);
        grBuildRecord.setResult(result);
        grBuildRecord.setJobName(jobName);
        grBuildRecord.setVersion(version);
        grBuildRecordMapper.insert(grBuildRecord);
        log.info("Groot ???????????????????????????{}", grBuildRecord.toString());
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
            // ???????????????????????????build????????????
            build = jenkinsService.getBuild(jobName, buildNum);
            grBuildRecordMapper.updateResult(jobName, buildNum, buildResult.name(), build.details().getDuration());
            log.info("???????????????????????????jobName={}, buildNum={}, result={}", jobName, buildNum, buildResult.name());
        } catch (Exception e) {
            log.error("jenkins ???????????????????????? (-.-#)", e);
        }
        GrBuildRecord grBuildRecord = grBuildRecordMapper.queryByBuild(jobName, buildNum);
        GrPublish grPublish = grPublishMapper.selectById(grBuildRecord.getPublishOid());
        //??????????????????

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
        log.info("?????????????????????, publish={}, status={}", grPublish.getOid(), grPublish.getPublishStatus());
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
        log.info("?????????????????????{}", grChangeLifeCircle.toString());
        if (EnvTypeEnum.test.value().equalsIgnoreCase(grPublish.getEnvType())
                && !"stable".equalsIgnoreCase(grPublish.getEnvCode())) {
            return;
        }
        sendDingTalkMessage(grPublish, grBuildRecord, buildResult);

        // ???????????????id???????????????????????????
        GetLinkedMemberNameByChangeIdResultDTO memberNameResultDTO = grMemberService.getLinkedMemberNameByChangeId(grPublish.getChangeId());

        // ????????????????????????????????????
        if (EnvTypeEnum.pro.value().equalsIgnoreCase(grPublish.getEnvType()) && StringUtils.isNotBlank(accessToken)) {
            // ????????????????????????
            this.sendRobotMessage(grPublish, grBuildRecord, buildResult, memberNameResultDTO);
        }

        if (buildResult.name().equalsIgnoreCase(BuildResult.SUCCESS.name()) &&
            EnvTypeEnum.pro.value().equalsIgnoreCase(grPublish.getEnvType()) &&
            "stable".equalsIgnoreCase(grPublish.getEnvCode())) {
            // ?????????????????????
            GrChange grChange = new GrChange();
            grChange.setOid(grPublish.getChangeId());
            grChange.setStatus(ChangeEnum.RELEASED.getCode());
            grChange.setUpdateUser("Groot");
            grChange.setUpdateTime(LocalDateTime.now());
            grChangeMapper.updateChangeStatus(grChange);

            // ??????????????????????????????????????????????????????????????????
            // ?????????????????????????????????, ????????????????????????????????????3h????????????owner+??????????????????6h??????????????????10h??????
            PublishSendNoticeDTO message = this.assembleMessage(grPublish);
            log.info("[????????????]???????????????message???{}", JSON.toJSONString(message));
            SendResult sendResult = mqSendNoticeService.sendNoticeMsg(JSON.toJSONString(message), PublishSendNoticePhaseEnum.ONE_PHASE.getCode());
            log.info("[????????????]???????????????sendResult???{}", JSON.toJSONString(sendResult));
        }
    }

    public PublishSendNoticeDTO assembleMessage(GrPublish grPublish) {
        PublishSendNoticeDTO message = new PublishSendNoticeDTO();
        message.setChangeId(grPublish.getChangeId());
        message.setPhaseCode(PublishSendNoticePhaseEnum.ONE_PHASE.getCode());
        message.setPublishUserId(grPublish.getOperatorId());

        // ????????????code??????????????????
        ApplicationParamAppCode appParam = new ApplicationParamAppCode();
        appParam.setAppCode(grPublish.getAppCode());
        ResponseData<ApplicationDTO> appResponse = milkywayClient.getApplicationInfoByAppCode(token, appParam);
        log.info("???????????????????????????{}", JSON.toJSONString(appResponse));
        ApplicationDTO appDTO = new ApplicationDTO();
        if (appResponse.isSuccess() && null != appResponse.getData()) {
            appDTO = appResponse.getData();
        }
        message.setOwnerId(appDTO.getOwnerId());
        // ???????????????id?????????????????????
        DomainArchitectureParamId domainParam = new DomainArchitectureParamId();
        domainParam.setOid(appDTO.getDomainArchitectureId());
        ResponseData<DomainArchitectureDTO> domainArchitectureResponse = milkywayClient.getDomainArchitectureInfoByOid(token, domainParam);
        log.info("??????????????????????????????{}", JSON.toJSONString(domainArchitectureResponse));
        DomainArchitectureDTO domainArchitectureDTO = new DomainArchitectureDTO();
        if (domainArchitectureResponse.isSuccess() && null != domainArchitectureResponse.getData()) {
            domainArchitectureDTO = domainArchitectureResponse.getData();
        }
        message.setDomainOwnerId(domainArchitectureDTO.getOwnerId());
        return message;
    }

    /**
     * ?????????????????????
     * @param grPublish
     * @param grBuildRecord
     * @param buildResult
     */
    private void sendRobotMessage(GrPublish grPublish, GrBuildRecord grBuildRecord, BuildResult buildResult,
        GetLinkedMemberNameByChangeIdResultDTO memberNameResultDTO) {
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype("markdown");
        OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
        markdown.setTitle("??????????????????");

        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append("## ?????????????????????").append("  \n  ");
        contentBuilder.append("**????????????**???").append(grPublish.getAppCode()).append("  \n  ");
        contentBuilder.append("**????????????**???").append(grPublish.getEnvType()).append("  \n  ");
        contentBuilder.append("**????????????**???").append(grPublish.getEnvCode()).append("  \n  ");
        contentBuilder.append("**????????????**???").append(buildResult.name()).append("  \n  ");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        contentBuilder.append("**????????????**???").append(grPublish.getReleaseTime().format(dateTimeFormatter)).append("  \n  ");
        contentBuilder.append("**????????????**???").append(grBuildRecord.getDuration() / 1000d).append(" s").append("  \n  ");
        contentBuilder.append("**????????????**???").append(LocalDateTime.now().format(dateTimeFormatter)).append("  \n  ");
        contentBuilder.append("**????????????**???").append(grBuildRecord.getCreateUser()).append("  \n  ");
        if (CollectionUtils.isNotEmpty(memberNameResultDTO.getDevelopmentMembers())) {
            contentBuilder.append("**????????????**???").append(Joiner.on(",").join(memberNameResultDTO.getDevelopmentMembers().iterator())).append("  \n  ");
        }
        if (CollectionUtils.isNotEmpty(memberNameResultDTO.getTestMembers())) {
            contentBuilder.append("**????????????**???").append(Joiner.on(",").join(memberNameResultDTO.getTestMembers().iterator())).append("  \n  ");
        }
        if (CollectionUtils.isNotEmpty(memberNameResultDTO.getCrMembers())) {
            contentBuilder.append("**CR??????**???").append(Joiner.on(",").join(memberNameResultDTO.getCrMembers().iterator())).append("  \n  ");
        }
        if (EnvTypeEnum.pro.value().equals(grPublish.getEnvType())) {
            contentBuilder.append("**????????????**???").append("???????????????????????????????????????????????????????????????????????????????????????????????????master").append("  \n  ");
        }
        log.info("?????????????????????????????????????????????{}", contentBuilder.toString());

        markdown.setText(contentBuilder.toString());
        request.setMarkdown(markdown);
        ResponseData<String> responseData = messageCenterProcessFeignClient.sendWebhookMessageNew(accessToken, request);
        if (responseData.isSuccess()) {
            log.info("??????????????????????????????????????????????????? {}", responseData.getData());
        } else {
            log.error("??????????????????????????????????????????????????? {}", responseData.getData());
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
            contentBuilder.append("**????????????**???").append(grPublish.getAppCode()).append("  \n  ");
            contentBuilder.append("**????????????**???").append(grPublish.getEnvType()).append("  \n  ");
            contentBuilder.append("**????????????**???").append(grPublish.getEnvCode()).append("  \n  ");
            contentBuilder.append("**????????????**???").append(buildResult.name()).append("  \n  ");
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            contentBuilder.append("**????????????**???").append(grPublish.getReleaseTime().format(dateTimeFormatter)).append("  \n  ");
            contentBuilder.append("**????????????**???").append(grBuildRecord.getDuration() / 1000d).append(" s").append("  \n  ");
            contentBuilder.append("**????????????**???").append(grBuildRecord.getCreateUser()).append("  \n  ");
            if (EnvTypeEnum.pro.value().equals(grPublish.getEnvType())) {
                contentBuilder.append("**????????????**???").append("???????????????????????????????????????????????????????????????????????????????????????????????????master").append("  \n  ");
            }
            msg.getMarkdown().setText(contentBuilder.toString());
            msg.getMarkdown().setTitle("Groot????????????");
            request.setMsg(msg);
            log.info("???????????????????????????{}", contentBuilder.toString());
            ResponseData<String> res = messageCenterProcessFeignClient.sendCorporationNotice(request);
            log.info("????????????????????????????????? {}", res.getMsg());
        }
    }
}
