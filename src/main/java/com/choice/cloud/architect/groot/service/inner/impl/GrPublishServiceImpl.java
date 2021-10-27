package com.choice.cloud.architect.groot.service.inner.impl;

import com.alibaba.fastjson.JSON;
import com.choice.cloud.architect.groot.dao.GrAppEnvRelMapper;
import com.choice.cloud.architect.groot.dao.GrChangeMapper;
import com.choice.cloud.architect.groot.dao.GrEnvMapper;
import com.choice.cloud.architect.groot.dao.GrPublishMapper;
import com.choice.cloud.architect.groot.dataobject.LastPublishQueryDO;
import com.choice.cloud.architect.groot.dataobject.PublishDeploymentRevisionQueryDO;
import com.choice.cloud.architect.groot.dataobject.PublishQueryDO;
import com.choice.cloud.architect.groot.dto.*;
import com.choice.cloud.architect.groot.enums.*;
import com.choice.cloud.architect.groot.exception.ServiceException;
import com.choice.cloud.architect.groot.gitlab.GitlabService;
import com.choice.cloud.architect.groot.model.*;
import com.choice.cloud.architect.groot.option.GlobalConst;
import com.choice.cloud.architect.groot.option.WebPage;
import com.choice.cloud.architect.groot.process.service.MessageCenterProcessFeignClient;
import com.choice.cloud.architect.groot.properties.EnableMonitorWhiteListProperties;
import com.choice.cloud.architect.groot.remote.AppListResponse;
import com.choice.cloud.architect.groot.remote.enums.FrameWorkEnum;
import com.choice.cloud.architect.groot.remote.jenkins.JenkinsService;
import com.choice.cloud.architect.groot.remote.milkyway.AppConfigInfoResponseDTO;
import com.choice.cloud.architect.groot.remote.milkyway.MilkywayService;
import com.choice.cloud.architect.groot.request.*;
import com.choice.cloud.architect.groot.response.ResponseData;
import com.choice.cloud.architect.groot.response.code.EnvResponseCode;
import com.choice.cloud.architect.groot.response.code.PublishResponseCode;
import com.choice.cloud.architect.groot.response.code.SysResponseCode;
import com.choice.cloud.architect.groot.service.inner.*;
import com.choice.cloud.architect.groot.support.AppDeployPodControllerTypeChooser;
import com.choice.cloud.architect.groot.support.PollingHandler;
import com.choice.cloud.architect.groot.util.CommonUtil;
import com.choice.driver.jwt.entity.AuthUser;
import com.ctrip.framework.apollo.ConfigService;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.offbytwo.jenkins.model.Artifact;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildWithDetails;
import com.offbytwo.jenkins.model.JobWithDetails;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.choice.cloud.architect.groot.enums.BuildResultEnum.BUILDING;
import static com.choice.cloud.architect.groot.enums.EnvTypeEnum.*;
import static com.choice.cloud.architect.groot.response.code.PublishResponseCode.SCALE_UP_AT_LEAST_ONE;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/3 16:49
 */
@Service
@Slf4j
public class GrPublishServiceImpl extends BaseService implements GrPublishService {

    //private static final String ACCESS_TOKEN = "ed608530428790de63142bcd8fb10f2414935428e875865125d87c22e6464983";

    @Value("${publish.robot.dingtalk.accesstoken}")
    private String accessToken;

    @Autowired
    private GrPublishMapper grPublishMapper;

    @Autowired
    private GrBuildRecordService grBuildRecordService;

    @Autowired
    private JenkinsService jenkinsService;

    @Autowired
    private MilkywayService milkywayService;

    @Autowired
    private GrPublishAuditService grPublishAuditService;

    @Autowired
    private PublishBlackListService publishBlackListService;

    @Autowired
    private GrAppEnvDefaultConfigService grAppEnvDefaultConfigService;

    @Autowired
    private GitlabService gitlabService;

    @Autowired
    private ContainerManageService containerManageService;

    @Autowired
    private GrEnvMapper grEnvMapper;

    @Autowired
    private GrChangeMapper grChangeMapper;

    @Autowired
    private PodOrchestrationService podOrchestrationService;

    @Autowired
    private GrPublishDetailService grPublishDetailService;

    @Autowired
    private PodManagementPermissionService podManagementPermissionService;

    @Autowired
    private AppManagePermissionService appManagePermissionService;

    @Autowired
    private AppDeployPodControllerTypeChooser appDeployPodControllerTypeChooser;

    @Autowired
    private GrAppEnvRelMapper grAppEnvRelMapper;

    @Value("${jenkins.job.template.version}")
    private String jenkinsTemplateVersion;

    @Value("${jenkins.job.template.name}")
    private String jenkinsTemplateName;

    @Value("${kubernetes.cluster.env}")
    private String k8sClusterEnv;

    @Value("${pro.publish.whitelist}")
    private String publishWhiteList;

    @Autowired
    private MessageCenterProcessFeignClient messageCenterProcessFeignClient;

    @Autowired
    private GrMemberService grMemberService;

    @Autowired
    private EnableMonitorWhiteListProperties enableMonitorWhiteListProperties;

    @Autowired
    Environment environment;

    @Override
    public WebPage<List<ListPublishDTO>> pageList(ListPublishRequest request) {
        Integer pageNum = request.getPageNum();
        Integer pageSize = request.getPageSize();
        List<ListPublishDTO> publishDTOList = Lists.newArrayList();
        PageHelper.startPage(pageNum, pageSize);
        PublishQueryDO pageParam = new PublishQueryDO();
        BeanUtils.copyProperties(request, pageParam);
        List<GrPublish> grPublishList = grPublishMapper.listWithPage(pageParam);

        if (CollectionUtils.isEmpty(grPublishList)) {
            return new WebPage<>(pageNum, pageSize, 0, null);
        }

        for (GrPublish grPublish : grPublishList) {
            ListPublishDTO listPublishDTO = new ListPublishDTO();
            BeanUtils.copyProperties(grPublish, listPublishDTO);
            listPublishDTO.setPublishStatus(grPublish.getPublishStatus());
            listPublishDTO.setTestStatus(grPublish.getTestStatus());
            publishDTOList.add(listPublishDTO);
        }

        PageInfo<GrPublish> info = new PageInfo<>(grPublishList);
        return new WebPage<>(pageNum, pageSize, info.getTotal(), publishDTOList);
    }

    @Override
    public int update(UpdatePublishRequest request, AuthUser authUser) {
        GrPublish grPublish = grPublishMapper.selectById(request.getOid());
        if (grPublish == null) {
            throw new ServiceException(PublishResponseCode.PUBLISH_NOT_FOUND);
        }
        BeanUtils.copyProperties(request, grPublish);
        grPublish.setUpdateTime(LocalDateTime.now());
        grPublish.setUpdateUser(currentUserName(authUser));
        int result = grPublishMapper.updateDynamic(grPublish);
        if (result < 1) {
            throw new ServiceException(PublishResponseCode.PUBLISH_UPDATE);
        }
        return result;
    }

    /**
     * 更改测试状态 并合并分支
     */
    @Deprecated
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTestStatusAndMergeMaster(UpdatePublishTestStatusRequest param, String updateUserName) {
        if (param == null || param.getOid() == null || param.getTestStatus() == null) {
            throw new ServiceException(SysResponseCode.PARAM_ILLEGAL);
        }
        PublishTestStatusEnum statusEnum = PublishTestStatusEnum.findByCode(param.getTestStatus());
        if (statusEnum == null) {
            throw new ServiceException(PublishResponseCode.TEST_STATUS_ERROR);
        }
        GrPublish publish = grPublishMapper.selectById(param.getOid());
        if (publish == null || GlobalConst.DELETE.equals(publish.getDeleteFlag())) {
            throw new ServiceException(PublishResponseCode.PUBLISH_NOT_FOUND);
        }
        // 只有 没有设置过测试状态，并且发布成功，才可以更改
        if (!PublishStatusEnum.SUCCESS.value().equals(publish.getPublishStatus())
                || PublishTestStatusEnum.GREEN.getCode().equals(publish.getTestStatus())
                || PublishTestStatusEnum.RED.getCode().equals(publish.getTestStatus())) {
            throw new ServiceException(PublishResponseCode.SET_TEST_STATUS_ERROR);
        }
        GrPublish request = new GrPublish();
        request.setId(publish.getId());
        request.setTestStatus(param.getTestStatus());
        request.setUpdateTime(LocalDateTime.now());
        request.setUpdateUser(updateUserName);
        int result = grPublishMapper.updateDynamic(request);
        log.info("GrPublishServiceImpl updateTestStatusAndMergeMaster 更新发布单测试状态 oid:{},result:{}", param.getOid(), result);
        // 测试通过,且为线上分支,且当前分支不是master，合并代码
        if (PublishTestStatusEnum.GREEN.getCode().equals(param.getTestStatus())
                && EnvTypeEnum.pro.value().equals(publish.getEnvType())
                && !"master".equals(publish.getGitBranch())) {
            gitlabService.merge(publish.getGitRepo(), publish.getGitBranch(), "master", "测试通过，合并分支，发布名称：" + publish.getChangeName());
            log.info("GrPublishServiceImpl updateTestStatusAndMergeMaster 合并分支 gitRepo:{},gitBranch:{}", publish.getGitRepo(), publish.getGitBranch());
        }

    }

    @Override
    public int create(CreatePublishRequest request) {
        GrPublish grPublish = new GrPublish();
        BeanUtils.copyProperties(request, grPublish);
        int result = grPublishMapper.insertDynamic(grPublish);
        if (result < 1) {
            throw new ServiceException("创建失败!");
        }
        return result;
    }

    @Override
    public PublishInfoDTO getByOid(String oid) {
        GrPublish grPublish = grPublishMapper.selectById(oid);
        PublishInfoDTO publishInfoDTO = new PublishInfoDTO();
        BeanUtils.copyProperties(grPublish, publishInfoDTO);
        GrAppEnvDefaultConfig config =
                grAppEnvDefaultConfigService.getAppEnvDefaultConfig(
                        grPublish.getAppCode(), grPublish.getEnvType(), grPublish.getEnvCode()
                );
        GrAppEnvDefaultConfigDTO configDTO = new GrAppEnvDefaultConfigDTO();
        if (config != null) {
            BeanUtils.copyProperties(config, configDTO);
        }
        publishInfoDTO.setConfig(configDTO);
        return publishInfoDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void close(String oid, AuthUser authUser) {
        GrPublish grPublish = grPublishMapper.selectById(oid);
        if (grPublish == null) {
            throw new ServiceException(PublishResponseCode.PUBLISH_NOT_FOUND);
        }
        grPublish.setUpdateTime(LocalDateTime.now());
        grPublish.setUpdateUser(currentUserName(authUser));
        grPublish.setPublishStatus(PublishStatusEnum.CLOSED.value());
        grPublishMapper.updateDynamic(grPublish);
    }

    /**
     * 执行发布流程
     *
     * @param publishRequest
     * @param authUser
     * @return
     */
    @Override
    public BuildResultDTO execute(PublishRequest publishRequest, AuthUser authUser) {
        String envType = publishRequest.getEnvType();
        String envCode = publishRequest.getEnvCode();
        String appCode = publishRequest.getAppCode();

        GrEnv grEnv = grEnvMapper.selectByCode(envType, envCode);

        if (null == grEnv) {
            throw new ServiceException(EnvResponseCode.ENV_RELATION_NOT_FOUND);
        }

        if (test.value().equalsIgnoreCase(envType)) {
            // 线下发布查询关联环境 ，如果不存在关联不允许发布
            List<GrAppEnvRel> grAppEnvRels = grAppEnvRelMapper
                    .listByAppCodeAndEnvCode(null, appCode, envType, envCode);
            if (CollectionUtils.isEmpty(grAppEnvRels)) {
                throw new ServiceException(EnvResponseCode.ENV_RELATION_NOT_FOUND);
            }
        }

        // 查询变更单
        GrChange grChange = grChangeMapper.selectById(publishRequest.getChangeId());

        // 如果是线下
        if (envType.equalsIgnoreCase(dev.value()) || envType.equalsIgnoreCase(test.value())) {
            // 创建审核并通过审核
            CreatePublishAuditRequest createPublishAuditRequest = new CreatePublishAuditRequest();
            createPublishAuditRequest.setChangeIds(Lists.newArrayList(publishRequest.getChangeId()));
            createPublishAuditRequest.setEnvCode(envCode);
            createPublishAuditRequest.setEnvName(grEnv.getEnvName());
            createPublishAuditRequest.setEnvType(envType);
            createPublishAuditRequest.setExpectPublishTime(LocalDateTime.now());
            createPublishAuditRequest.setPublishName(grChange.getChangeName());
            createPublishAuditRequest.setPublishType(PublishTypeEnum.DAILY.value());
            createPublishAuditRequest.setPublishDesc("快速审核通过");
            grPublishAuditService.createAndSubmitPublishAudit(createPublishAuditRequest, authUser);
        } else { // 线上

            // 如果是线下groot不能发线上
            if (!"online".equals(k8sClusterEnv)) {
                throw new ServiceException(PublishResponseCode.CAN_NOT_ONLINE_PUBLISH);
            }

            appManagePermissionService.checkPermission(authUser, grChange.getAppCode());


//            GrPublishAudit grPublishAudit = grPublishAuditService.getLastAuditByChangeIdAndEnv(grChange.getOid(), envType, envCode);
//            if (grPublishAudit == null ) {
//                //没有审核直接跳转
//                return abnormalPublishResult(PublishResponseCode.PUBLISH_AUDIT_NOT_FOUND);
//            }

            // TODO: 2020/5/12 增加白名单
            // pre 测试未通过，抛出异常
//            List<String> whiteList = null;
//            if (StringUtils.isNotBlank(publishWhiteList)) {
//                whiteList = Lists.newArrayList(publishWhiteList.split(","));
//            }
//
//            if(CollectionUtils.isNotEmpty(whiteList) && !whiteList.contains(appCode)) {
//                if (envCode.equalsIgnoreCase(pre.value())) {
//                    // 未进入PRE阶段
//                    if (!grChange.getStage().equalsIgnoreCase(pre.value())) {
//                        throw new ServiceException(ChangeResponseCode.PRE_STAGE_ERROR);
//                    }
//                } else {
//                    // pro 发布
//                    if (!grChange.getStage().equalsIgnoreCase(pro.value())) {
//                        throw new ServiceException(ChangeResponseCode.PRO_STAGE_ERROR);
//                    }
//                    // pre最后一次发布不是成功状态，抛出异常
//                    GrPublish lastPrePublish = grPublishMapper.lastByAppEnv(appCode, pro.value(), pre.value(), GlobalConst.NOT_DELETE);
//                    if (!lastPrePublish.getPublishStatus().equalsIgnoreCase(PublishStatusEnum.SUCCESS.value())) {
//                        throw new ServiceException(ChangeResponseCode.PRO_PUBLISH_ERROR);
//                    }
//                }
//            }
        }
        LastPublishQueryDO queryDO = new LastPublishQueryDO();
        queryDO.setAppCode(appCode);
        queryDO.setChangeId(publishRequest.getChangeId());
        queryDO.setEnvCode(envCode);
        queryDO.setEnvType(envType);
        queryDO.setDeleteFlag(GlobalConst.NOT_DELETE);
        queryDO.setPublishStatus(PublishStatusEnum.WAIT_PUBLISH.value());
        // 获取publish 对象
        GrPublish grPublish = grPublishMapper.lastByChangeAndEnv(queryDO);
        // 如果没有则返回  前端跳转到审核页面
        if (grPublish == null) {
            return abnormalPublishResult(PublishResponseCode.PUBLISH_AUDIT_ERROR);
        }

        // 创建构建请求参数
        PublishBuildRequest publishBuildRequest = new PublishBuildRequest();
        GrAppEnvDefaultConfig grAppEnvDefaultConfig =
                grAppEnvDefaultConfigService.getAppEnvDefaultConfig(appCode, envType, envCode);
        boolean envConfig = true;
        boolean appConfig = true;
        AppConfigInfoResponseDTO infoByAppCode = new AppConfigInfoResponseDTO();
        if (grAppEnvDefaultConfig != null) {
            BeanUtils.copyProperties(grAppEnvDefaultConfig, publishBuildRequest);
        } else {
            log.info("根据应用编码信息没有获取到相应的环境配置");
            envConfig = false;
            // 环境配置没有配置的话就去找应用配置信息
            // 根据应用编码获取应用配置信息【应用角度】
            infoByAppCode = milkywayService.getAppConfigInfoByAppCode(appCode);
            if (Objects.isNull(infoByAppCode)) {
                log.info("根据应用编码信息没有获取到相应的应用配置");
                appConfig = false;
            }
        }
        publishBuildRequest.setOid(grPublish.getOid());

        List<String> podNameList = Lists.newArrayList();
        List<PublishPodDetailDTO> publishPodDetails = publishRequest.getPublishPodDetails();
        if (CollectionUtils.isNotEmpty(publishPodDetails)) {
            podNameList = publishPodDetails.stream()
                    .map(PublishPodDetailDTO::getPodName)
                    .collect(Collectors.toList());
        }

        PodControllerTypeEnum podControllerTypeEnum = appDeployPodControllerTypeChooser.choose(grPublish.getAppCode());

        if (podControllerTypeEnum.equals(PodControllerTypeEnum.STATEFUL_SET)) {
            // 再次检查是否是第一次构建
            String firstCheck = this.checkFirstPublish(appCode, envType, envCode);
            log.info("envType = {}, envCode = {}, appCode = {}, 是否是第一次构建 = {}", envType, envCode, appCode, firstCheck);
            String podOrchestrationJson = podOrchestrationService.orchestratePod(envType, envCode, appCode, publishRequest.getFirstPublish(), podNameList);
            publishBuildRequest.setPodOrchestration(podOrchestrationJson);
        }

        // 触发构建
        return build(publishBuildRequest, authUser, envConfig, appConfig, infoByAppCode, publishRequest);
    }

    /**
     * 机器人通知
     *
     * @param envType
     * @param envCode
     * @param appCode
     * @param currentUserName
     */
    private void sendRobotMessage(String envType, String envCode, String appCode, String currentUserName,
                                  GetLinkedMemberNameByChangeIdResultDTO memberNameResultDTO) {
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype("markdown");
        OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
        markdown.setTitle("开始发布应用");

        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append("## 开始发布应用：").append("  \n  ");
        contentBuilder.append("**应用名称**：").append(appCode).append("  \n  ");
        contentBuilder.append("**环境类型**：").append(envType).append("  \n  ");
        contentBuilder.append("**服务分组**：").append(envCode).append("  \n  ");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        contentBuilder.append("**开始发布时间**：").append(LocalDateTime.now().format(dateTimeFormatter)).append("  \n  ");
        contentBuilder.append("**发布人员**：").append(currentUserName).append("  \n  ");
        if (CollectionUtils.isNotEmpty(memberNameResultDTO.getDevelopmentMembers())) {
            contentBuilder.append("**开发人员**：").append(Joiner.on(",").join(memberNameResultDTO.getDevelopmentMembers().iterator())).append("  \n  ");
        }
        if (CollectionUtils.isNotEmpty(memberNameResultDTO.getTestMembers())) {
            contentBuilder.append("**测试人员**：").append(Joiner.on(",").join(memberNameResultDTO.getTestMembers().iterator())).append("  \n  ");
        }
        if (CollectionUtils.isNotEmpty(memberNameResultDTO.getCrMembers())) {
            contentBuilder.append("**CR人员**：").append(Joiner.on(",").join(memberNameResultDTO.getCrMembers().iterator())).append("  \n  ");
        }
        log.info("开始发布，发送机器人消息：{}", contentBuilder.toString());

        markdown.setText(contentBuilder.toString());
        request.setMarkdown(markdown);
        ResponseData<String> responseData = messageCenterProcessFeignClient.sendWebhookMessageNew(accessToken, request);
        if (responseData.isSuccess()) {
            log.info("开始发布，发送机器人消息响应： {}", responseData.getData());
        } else {
            log.error("开始发布，发送机器人消息响应失败： {}", responseData.getData());
        }
    }

    private BuildResultDTO abnormalPublishResult(PublishResponseCode responseCode) {
        BuildResultDTO buildResultDTO = new BuildResultDTO();
        buildResultDTO.setCode(responseCode.getCode());
        buildResultDTO.setMessage(responseCode.getDesc());
        return buildResultDTO;
    }

    @Override
    public BuildResultDTO build(PublishBuildRequest request, AuthUser authUser,
                                boolean envConfig, boolean appConfig, AppConfigInfoResponseDTO infoByAppCode, PublishRequest publishRequest) {

        // 判断并更新发布单状态
        GrPublish grPublish = grPublishMapper.selectById(request.getOid());
        if (null == grPublish) {
            throw new ServiceException("发布npe");
        }
        if (!grPublish.getPublishStatus().equalsIgnoreCase(PublishStatusEnum.WAIT_PUBLISH.value())) {
            throw new ServiceException(PublishResponseCode.WRONG_PUBLISH_STATUS);
        }

        if (grPublish.getEnvType().equals(EnvTypeEnum.pro.value())) {

            LocalDateTime now = LocalDateTime.now();


            PublishAuditDetailDTO publishAuditDetailDTO = grPublishAuditService.getPublishAuditDetail(grPublish.getPublishAuditId());
            LocalDateTime exceptPublishTime = publishAuditDetailDTO.getExpectPublishTime();

            // 如果线上日常发布,则判断发布时间是否符合规则,在黑名单的不允许发布,只能走故障发布
            if (publishBlackListService.publishTimeIsInBlackList(now) && !publishAuditDetailDTO.getPublishType().equals(PublishTypeEnum.FAULT.value())) {
                throw new ServiceException(PublishResponseCode.PUBLISH_TIME_ILLEGAL);
            }

            long hourPeriod = 8;

            // 不在发布时间窗口内  8hours
            if (exceptPublishTime.plusHours(hourPeriod).isBefore(now)) {
                // 更新此发布单状态
                grPublish.setPublishStatus(PublishStatusEnum.CLOSED.value());
                grPublishMapper.updateDynamic(grPublish);
                throw new ServiceException(PublishResponseCode.EXCEPT_PUBLISH_TIME_ILLEGAL);
            }
        }
        AppListResponse app = milkywayService.getAppInfo(grPublish.getAppCode());
        if (null == app) {
            throw new ServiceException("应用npe");
        }

        // 根据约定获取jobName
        String jobName = getJobName(grPublish.getAppCode(), jenkinsTemplateVersion);

        // 判断构建记录中是否有同名job正在构建（正常构建）
        if (grBuildRecordService.existNormalBuilding(grPublish.getAppCode())) {
            throw new ServiceException(PublishResponseCode.JOB_IS_BUILDING);
        }
        // 获取job
        JobWithDetails job = getOrCreateJob(jenkinsTemplateName, jobName);

        // 处理发布结果
        return handlePublishResult(jobName, job, grPublish, request, authUser, app, envConfig, appConfig, infoByAppCode, publishRequest);
    }

    @Transactional(rollbackFor = Exception.class)
    public BuildResultDTO handlePublishResult(String jobName, JobWithDetails job, GrPublish grPublish, PublishBuildRequest request, AuthUser authUser, AppListResponse app,
                                              boolean envConfig, boolean appConfig, AppConfigInfoResponseDTO infoByAppCode, PublishRequest publishRequest) {
        int buildNum = job.getNextBuildNumber();

        // 更新发布单
        grPublish.setPublishStatus(PublishStatusEnum.DEPLOYING.value());
        grPublish.setReleaseTime(LocalDateTime.now());
        grPublish.setBuildNum(buildNum);
        grPublish.setUpdateUser(getUserName(authUser));
        grPublish.setUpdateTime(LocalDateTime.now());
        // 最后操作人id
        grPublish.setOperatorId(authUser.getUid());
        grPublishMapper.updateDynamic(grPublish);

        // 线上环境，包括生产和预发 需要合并代码  jar不用合
        if (EnvTypeEnum.pro.value().equals(grPublish.getEnvType())
                && !FrameWorkEnum.FW_JAR.getCode().equalsIgnoreCase(app.getFramework())) {
            gitlabService.merge(grPublish.getGitRepo(), "master", grPublish.getGitBranch(), "发布之前合并master到此分支，发布名称：" + grPublish.getPublishName());
        }

        // 生成构建参数
        Map<String, String> param = generateBuildParam(request, grPublish, app, envConfig, appConfig, infoByAppCode);

        // 根据变更单id查询关联的人员信息
        GetLinkedMemberNameByChangeIdResultDTO memberNameResultDTO = grMemberService.getLinkedMemberNameByChangeId(publishRequest.getChangeId());

        String currentUserName = CommonUtil.currentUserId();

        // 开始发布，进行机器人通知,只有生产才会发送
        if (pro.value().equalsIgnoreCase(publishRequest.getEnvType()) && StringUtils.isNotBlank(accessToken)) {
            this.sendRobotMessage(publishRequest.getEnvType(), publishRequest.getEnvCode(), publishRequest.getAppCode(), currentUserName, memberNameResultDTO);
        }

        // 触发构建
        jenkinsService.buildJob(job, param);
        BuildResultDTO buildResultDTO = new BuildResultDTO();
        buildResultDTO.setJobName(jobName);
        buildResultDTO.setBuildNum(buildNum);
        buildResultDTO.setBuildId(String.valueOf(buildNum));
        buildResultDTO.setPublishOid(grPublish.getOid());
        log.info("触发jenkins构建 jobName：{}， num：{}, username:{}, publishId:{}", jobName, buildNum, getUserName(authUser), grPublish.getOid());

        // 构建记录
        grBuildRecordService.saveBuildRecord(jobName, grPublish.getOid(), grPublish.getAppCode(), BUILDING.getValue(),
                buildNum, jenkinsTemplateVersion, authUser);

        // 记录发布单详情
        PublishDetailCreateDTO publishDetailCreateDTO = new PublishDetailCreateDTO();
        publishDetailCreateDTO.setPublishId(grPublish.getOid());
        List<PublishPodDetailDTO> publishPodDetailDTOS = JSON.parseArray(request.getPodOrchestration(), PublishPodDetailDTO.class);
        publishDetailCreateDTO.setPublishPodDetails(publishPodDetailDTOS);
        grPublishDetailService.savePublishDetail(publishDetailCreateDTO, authUser);

        return buildResultDTO;
    }

    private String getJobName(String appCode, String version) {
        return appCode + "-" + version;
    }

    private JobWithDetails getOrCreateJob(String template, String jobName) {

        JobWithDetails jobWithDetails = jenkinsService.getJobInfoByName(jobName);
        if (jobWithDetails == null) {
            // 如果不存在，则新建
            jenkinsService.createJobFrom(jobName, template);
        }
        // 轮询等待创建成功
        try {
            jobWithDetails = new PollingHandler<JobWithDetails>().process(
                    10, 1, () -> jenkinsService.getJobInfoByName(jobName)
            );
        } catch (Exception e) {
            jobWithDetails = null;
        }

        if (jobWithDetails == null) {
            throw new ServiceException(PublishResponseCode.JENKINS_RESPONSE_SLOW);
        }

        return jobWithDetails;
    }


    private Map<String, String> generateBuildParam(PublishBuildRequest request, GrPublish grPublish, AppListResponse response,
                                                   boolean envConfig, boolean appConfig, AppConfigInfoResponseDTO infoByAppCode) {
        Map<String, String> param = Maps.newHashMap();
        param.put("cluster", getCluster(grPublish.getEnvType()));
        param.put("namespace", getNameSpace(grPublish.getEnvType(), grPublish.getEnvCode()));
        param.put("env_type", grPublish.getEnvType());
        param.put("appname", grPublish.getAppCode());
        param.put("repo", grPublish.getGitRepo());
        param.put("branch", grPublish.getGitBranch());
        param.put("apollo_env", grPublish.getApolloEnv());
        param.put("apollo_idc", grPublish.getApolloIdc());
        param.put("current_env", getCurrentEnv(grPublish.getEnvType()));
        param.put("container_name", grPublish.getAppCode());
        param.put("ac_switch", "pinpoint-off");
        // 环境配置存在的话
        if (!envConfig && appConfig) {
            param.put("healthcheck_port", String.valueOf(infoByAppCode.getHealthCheckPort()));
            param.put("healthcheck_path", infoByAppCode.getHealthCheckPath());
            param.put("healthcheck_initialDelaySeconds", infoByAppCode.getHealthCheckInitialDelaySeconds());
            param.put("healthcheck_periodSeconds", infoByAppCode.getHealthCheckPeriodSeconds());
            param.put("healthcheck_successThreshold", infoByAppCode.getHealthCheckSuccessThreshold());
            param.put("healthcheck_failureThreshold", infoByAppCode.getHealthCheckFailureThreshold());
            param.put("healthcheck_timeoutSeconds", infoByAppCode.getHealthCheckTimeoutSeconds());
            param.put("sonar", String.valueOf(infoByAppCode.getSonar()));
            param.put("sonar_path", infoByAppCode.getSonarPath());
            param.put("javaoptions", StringUtils.isBlank(infoByAppCode.getJavaOptions())
                    ? GlobalConst.JVM_OPT
                    : infoByAppCode.getJavaOptions()
            );
        } else {
            param.put("healthcheck_port", String.valueOf(request.getHealthCheckPort()));
            param.put("healthcheck_path", request.getHealthCheckPath());
            param.put("healthcheck_initialDelaySeconds", String.valueOf(request.getHealthCheckInitialDelaySeconds()));
            param.put("healthcheck_periodSeconds", String.valueOf(request.getHealthCheckPeriodSeconds()));
            param.put("healthcheck_successThreshold", String.valueOf(request.getHealthCheckSuccessThreshold()));
            param.put("healthcheck_failureThreshold", String.valueOf(request.getHealthCheckFailureThreshold()));
            param.put("healthcheck_timeoutSeconds", String.valueOf(request.getHealthCheckTimeoutSeconds()));
            param.put("sonar", String.valueOf(request.getSonar()));
            param.put("sonar_path", request.getSonarPath());
            param.put("javaoptions", StringUtils.isBlank(request.getJavaOptions())
                    ? GlobalConst.JVM_OPT
                    : request.getJavaOptions()
            );
        }

        appendJavaOptions(param, grPublish.getEnvType());

        param.put("sonar_path", StringUtils.isNotBlank(response.getJarFilePath()) && param.get("sonar_path").equals("./")
                ? response.getJarFilePath() : param.get("sonar_path"));
        param.put("jarfile_path", StringUtils.isBlank(response.getJarFilePath()) ? "./" : response.getJarFilePath());
        param.put("sonarkey", grPublish.getGitBranch().replace("/", "-"));
        param.put("service_group", grPublish.getEnvCode());
        param.put("mq_gray_switch", grPublish.getMqGroupSwitch());
        param.put("framework", response.getFramework() == null ? FrameWorkEnum.FW_MAVEN.getCode() : response.getFramework());
        param.put("mq_gray_producer_switch", request.getMqGrayProducerSwitch());
        param.put("mq_gray_consumer_switch", request.getMqGrayConsumerSwitch());
        param.put("pod_json", request.getPodOrchestration() == null ? "" : request.getPodOrchestration());
        param.put("pod_controller_type", appDeployPodControllerTypeChooser.choose(grPublish.getAppCode()).value());

        log.info("Jenkins构建参数={}", JSON.toJSONString(param));
        return param;
    }

    /**
     * 追加 JavaOptions 参数.
     * <p>
     * 1.只针对在 Apollo 配置中 "app.white" 中存在的服务生效.
     * 2.添加 adminconsole & pinpoint javaagent 路径.
     * 3.区分并添加 pinpoint profile.
     *
     * @param param   环境变量 map.
     * @param envType 所处环境.
     */
    private void appendJavaOptions(Map<String, String> param, String envType) {

//        String whiteList = enableMonitorWhiteListProac_switchperties.getWhiteList();
        String whiteList = environment.getProperty("app.white", "");
        boolean hasPermission = Splitter.on(",").splitToList(whiteList).contains(param.get("appname"));
        log.info("whiteList:{}",whiteList);
        log.info("appname:{}",param.get("appname"));
        if (hasPermission) {
            String healthcheckPath = param.get("healthcheck_path");
            String javaoptions = param.get("javaoptions");
            if (envType.equalsIgnoreCase(pre.value()) || envType.equalsIgnoreCase(pro.value())) {
                param.put("javaoptions", javaoptions + " -javaagent:/agent/pinpoint-agent/pinpoint-bootstrap.jar -javaagent:/agent/dest/agent-bootstrap.jar -Dpinpoint.profiler.profiles.active=prod -Dhealth.check.path=" + healthcheckPath);
            } else {
                param.put("javaoptions", javaoptions + " -javaagent:/agent/pinpoint-agent/pinpoint-bootstrap.jar -javaagent:/agent/dest/agent-bootstrap.jar -Dpinpoint.profiler.profiles.active=test -Dhealth.check.path=" + healthcheckPath);
            }
            param.put("ac_switch", "pinpoint-on");
        }
    }

    private String getCurrentEnv(String envType) {
        if (envType.equalsIgnoreCase(pre.value()) || envType.equalsIgnoreCase(pro.value())) {
            return "csprod";
        } else {
            return "cstest";
        }
    }

    private String getNameSpace(String envType, String envCode) {
        return envType + "-" + envCode;
    }

    private String getCluster(String envType) {
        if (envType.equalsIgnoreCase(pre.value()) || envType.equalsIgnoreCase(pro.value())) {
            return ClusterEnum.ONLINE.value();
        } else {
            return ClusterEnum.OFFLINE.value();
        }
    }


    @Override
    public BuildInfoDTO getBuildInfo(String appCode, String envType, String envCode) {
        GrPublish grPublish = grPublishMapper.lastByAppEnv(appCode, envType, envCode, GlobalConst.NOT_DELETE);
        if (grPublish == null) {
            return null;
        }
        return getBuildInfo(grPublish);
    }

    @Override
    public BuildInfoDTO getBuildInfo(String oid) {
        GrPublish grPublish = grPublishMapper.selectById(oid);
        if (grPublish == null) {
            return null;
        }
        return getBuildInfo(grPublishMapper.selectById(oid));
    }

    @Override
    public BuildInfoDTO getBuildInfo(GrPublish grPublish) {
        GrBuildRecord grBuildRecord = grBuildRecordService.getLastRecordByPublishOid(grPublish.getOid());
        if (grBuildRecord == null) {
            throw new ServiceException(PublishResponseCode.BUILD_RECORD_NOT_FOUND);
        }
        int num = grBuildRecord.getBuildNum();
        BuildInfoDTO buildInfoDTO = new BuildInfoDTO();
        String jobName = grBuildRecord.getJobName();
        BeanUtils.copyProperties(grPublish, buildInfoDTO);
        buildInfoDTO.setJobName(jobName);
        buildInfoDTO.setBuildNum(num);
        buildInfoDTO.setTemplateVersion(grBuildRecord.getVersion());
        log.info("getBuildInfo -> jobName:{}, buildNum:{}", jobName, num);
        Build build = jenkinsService.getBuild(jobName, num);
        BuildWithDetails buildWithDetails;
        try {
            buildWithDetails = build.details();
        } catch (Exception e) {
            log.warn("Detail-> jobName:{}, num:{}  not found", jobName, num);
            throw new ServiceException(PublishResponseCode.JENKINS_BUILD_NOT_FOUND);
        }
        buildInfoDTO.setBuildId(buildWithDetails.getId());
        buildInfoDTO.setBuilding(buildWithDetails.isBuilding());
        buildInfoDTO.setDuration(buildWithDetails.getDuration() / 1000L);
        buildInfoDTO.setBuildTime(grBuildRecord.getCreateTime());
        List<Artifact> artifacts = buildWithDetails.getArtifacts();
        String artifactFile = null;
        if (CollectionUtils.isNotEmpty(artifacts)) {
            for (Artifact a : artifacts) {
                artifactFile = "," + a.getFileName();
            }
            artifactFile = artifactFile.substring(1);
        }
        buildInfoDTO.setArtifacts(artifactFile);
        buildInfoDTO.setNodes(jenkinsService.getBuildNodes(jobName, num));
        return buildInfoDTO;
    }

    @Override
    public BuildInfoDTO getLastBuildInfo(String changeOid) {

        return null;
    }

    @Override
    public String stopBuildByNumber(String jobName, int buildNum) {
        return jenkinsService.stopBuildByNumber(jobName, buildNum);
    }

    @Override
    public String getBuildNodeLog(String jobName, int buildNum, int nodeId) {
        try {
            return jenkinsService.getNodeLogs(jobName, buildNum, nodeId);
        } catch (Exception e) {
            return "";
        }

    }

    @Override
    public void rollBackToPreviousRevision(DeploymentRollbackRequest request, AuthUser authUser) {
        String appCode = request.getAppCode();
        String envType = request.getEnvType();
        String envCode = request.getEnvCode();

        // 判断当前是否有构建，如果有则不允许回滚
        GrPublish grPublish = grPublishMapper.lastByPublish(appCode, envType, envCode,
                GlobalConst.NOT_DELETE, PublishStatusEnum.DEPLOYING.value());
        if (grPublish != null) {
            LocalDateTime releaseTime = grPublish.getReleaseTime();
            final int delayPublishTime = 10;
            boolean canNotRollback = releaseTime.plusMinutes(delayPublishTime).isAfter(LocalDateTime.now());
            if (canNotRollback) {
                throw new ServiceException(PublishResponseCode.ROLLBACK_DEPLOYING_ILLEGAL);
            }
        }

        String currentRevision = containerManageService.getDeploymentRevision(envType, envCode, appCode);
        Integer lastRevision = Integer.parseInt(currentRevision) - 1;
        PublishDeploymentRevisionQueryDO queryDO = new PublishDeploymentRevisionQueryDO();
        queryDO.setAppCode(appCode);
        queryDO.setEnvType(envType);
        queryDO.setEnvCode(envCode);
        queryDO.setDeleteFlag(GlobalConst.NOT_DELETE);
        queryDO.setPublishStatus(PublishStatusEnum.SUCCESS.value());
        queryDO.setDeploymentRevision(String.valueOf(lastRevision));

        GrPublish lastRevisionPublish = grPublishMapper.lastByPublishRevision(queryDO);

        log.info("执行回滚（rollback）,appCode={}, envType={}, envCode={}, username={}, uid={}",
                appCode, envType, envCode, getUserName(authUser), authUser.getUid());
        containerManageService.rollbackToPreviousRevision(getNameSpace(envType, envCode), appCode);

        if (null != lastRevisionPublish) {
            // 更新回滚版本
            Integer newRevision = Integer.parseInt(currentRevision) + 1;
            lastRevisionPublish.setDeploymentRevision(String.valueOf(newRevision));
            grPublishMapper.updateDynamic(lastRevisionPublish);
        }
    }


    @Override
    public void rollbackToSpecifiedRevision(DeploymentRollbackRequest request, AuthUser authUser) {
        String appCode = request.getAppCode();
        String envType = request.getEnvType();
        String envCode = request.getEnvCode();

        // 判断当前是否有构建，如果有则不允许回滚
        GrPublish lastGrPublish = grPublishMapper.lastByPublish(appCode, envType, envCode,
                GlobalConst.NOT_DELETE, PublishStatusEnum.DEPLOYING.value());
        if (lastGrPublish != null) {
            LocalDateTime releaseTime = lastGrPublish.getReleaseTime();
            final int delayPublishTime = 10;
            boolean canNotRollback = releaseTime.plusMinutes(delayPublishTime).isAfter(LocalDateTime.now());
            if (canNotRollback) {
                throw new ServiceException(PublishResponseCode.ROLLBACK_DEPLOYING_ILLEGAL);
            }
        }
        log.info("执行回滚（rollback）,appCode={}, envType={}, envCode={}, username={}, uid={}",
                appCode, envType, envCode, getUserName(authUser), authUser.getUid());
        String currentRevision = containerManageService.getDeploymentRevision(envType, envCode, appCode);
        GrPublish grPublish = grPublishMapper.selectById(request.getPublishOid());
        if (currentRevision.equals(grPublish.getDeploymentRevision())) {
            throw new ServiceException(PublishResponseCode.ROLLBACK_SAME_REVISION);
        }

        containerManageService.rollbackToSpecifiedRevision(getNameSpace(envType, envCode), appCode, grPublish.getDeploymentRevision());

        // 更新回滚版本
        Integer newRevision = Integer.parseInt(currentRevision) + 1;
        grPublish.setDeploymentRevision(String.valueOf(newRevision));
        grPublishMapper.updateDynamic(grPublish);

    }

    @Deprecated
    @Async("alarmNoticeTaskExecutor")
    public void updateRollbackRevision(GrPublish grPublish) {
        int i = 0;
        String revision;
        do {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                log.warn("延时阻断", e);
            }
            revision = containerManageService
                    .getDeploymentRevision(grPublish.getEnvType(), grPublish.getEnvCode(), grPublish.getAppCode());
            i++;
        } while (grPublish.getDeploymentRevision().equals(revision) && i < 5);

        grPublish.setDeploymentRevision(revision);
        grPublishMapper.updateDynamic(grPublish);
    }


    @Override
    public String checkFirstPublish(String appCode, String envType, String envCode) {
        String namespace = envType.concat("-").concat(envCode);
        QueryPodListRequest queryPodListRequest = new QueryPodListRequest();
        queryPodListRequest.setNamespaceName(namespace);
        queryPodListRequest.setAppCode(appCode);
        List<ListPodInfoDTO> podListDTOList = containerManageService.statefulSetTypePodList(queryPodListRequest);

        if (CollectionUtils.isEmpty(podListDTOList) && appDeployPodControllerTypeChooser.choose(appCode).equals(PodControllerTypeEnum.STATEFUL_SET)) {
            return "TRUE";
        } else {
            return "FALSE";
        }
    }

    @Override
    public List<ApplicationDeployInfoDTO> getApplicationDeployInfo(String appCode, String envType, String envCode) {
        List<ApplicationDeployInfoDTO> result = Lists.newArrayList();

        QueryPodListRequest queryPodListRequest = buildQueryPodListRequest(appCode, envType, envCode);
        List<ListPodInfoDTO> podListDTOList = containerManageService.podList(queryPodListRequest);

        for (ListPodInfoDTO listPodListDTO : podListDTOList) {
            ApplicationDeployInfoDTO applicationDeployInfoDTO = new ApplicationDeployInfoDTO();
            BeanUtils.copyProperties(listPodListDTO, applicationDeployInfoDTO);
            result.add(applicationDeployInfoDTO);
        }

        return result;
    }

    @Override
    public void scaleUpPod(ScaleUpPodRequest request, AuthUser authUser) {
        if (!podManagementPermissionService.hasPermission(authUser)) {
            throw new ServiceException("无权限操作，请联系运维人员处理");
        }

        if (request.getScaleUpNum() == 0) {
            throw new ServiceException(SCALE_UP_AT_LEAST_ONE);
        }

        // 获取扩容pod编排信息
        OrchestratePodWhenScaleUpDTO orchestratePodWhenScaleUpDTO = new OrchestratePodWhenScaleUpDTO();
        BeanUtils.copyProperties(request, orchestratePodWhenScaleUpDTO);
        String scaleUpPodJsonInfo = podOrchestrationService.orchestratePodWhenScaleUp(orchestratePodWhenScaleUpDTO);

        log.info("user = {}, idc = {}, ldc = {}, scaleUpNum = {}, scaleUpPodJsonInfo = {}", currentUserName(authUser), request.getIdc(), request.getLdc(), request.getScaleUpNum(), scaleUpPodJsonInfo);
    }

    private QueryPodListRequest buildQueryPodListRequest(String appCode, String envType, String envCode) {
        String namespace = envType.concat("-").concat(envCode);
        QueryPodListRequest queryPodListRequest = new QueryPodListRequest();
        queryPodListRequest.setNamespaceName(namespace);
        queryPodListRequest.setAppCode(appCode);

        return queryPodListRequest;
    }
}
