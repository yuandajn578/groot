package com.choice.cloud.architect.groot.service.inner.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;

import com.aliyun.openservices.ons.api.SendResult;
import com.choice.cloud.architect.groot.convertor.ChangeConvertor;
import com.choice.cloud.architect.groot.dao.GrChangeLifeCircleMapper;
import com.choice.cloud.architect.groot.dao.GrChangeMapper;
import com.choice.cloud.architect.groot.dao.GrMemberMapper;
import com.choice.cloud.architect.groot.dao.GrPublishMapper;
import com.choice.cloud.architect.groot.dto.ListChangeDTO;
import com.choice.cloud.architect.groot.enums.BranchChangeEnum;
import com.choice.cloud.architect.groot.enums.ChangeEnum;
import com.choice.cloud.architect.groot.enums.EnvTypeEnum;
import com.choice.cloud.architect.groot.enums.MemberEnum;
import com.choice.cloud.architect.groot.enums.OperateResultEnum;
import com.choice.cloud.architect.groot.enums.OperateTypeEnum;
import com.choice.cloud.architect.groot.enums.PublishSendNoticePhaseEnum;
import com.choice.cloud.architect.groot.enums.PublishStatusEnum;
import com.choice.cloud.architect.groot.enums.RelOrderTypeEnum;
import com.choice.cloud.architect.groot.exception.ServiceException;
import com.choice.cloud.architect.groot.gitlab.GitlabService;
import com.choice.cloud.architect.groot.model.GrChange;
import com.choice.cloud.architect.groot.model.GrChangeLifeCircle;
import com.choice.cloud.architect.groot.model.GrMember;
import com.choice.cloud.architect.groot.model.GrPublish;
import com.choice.cloud.architect.groot.mq.publish.MqSendNoticeService;
import com.choice.cloud.architect.groot.mq.publish.PublishSendNoticeDTO;
import com.choice.cloud.architect.groot.option.GlobalConst;
import com.choice.cloud.architect.groot.option.WebPage;
import com.choice.cloud.architect.groot.process.service.MessageCenterProcessFeignClient;
import com.choice.cloud.architect.groot.properties.NoPreAppWhiteListProperties;
import com.choice.cloud.architect.groot.remote.AppConfigRelationDTO;
import com.choice.cloud.architect.groot.remote.AppListResponse;
import com.choice.cloud.architect.groot.remote.DomainArchitectureDTO;
import com.choice.cloud.architect.groot.remote.enums.FrameWorkEnum;
import com.choice.cloud.architect.groot.remote.milkyway.ApplicationDTO;
import com.choice.cloud.architect.groot.remote.milkyway.ApplicationParamAppCode;
import com.choice.cloud.architect.groot.remote.milkyway.DomainArchitectureParamId;
import com.choice.cloud.architect.groot.remote.milkyway.MilkywayClient;
import com.choice.cloud.architect.groot.remote.milkyway.MilkywayService;
import com.choice.cloud.architect.groot.request.CreateChangeRequest;
import com.choice.cloud.architect.groot.request.CreateMemberRequest;
import com.choice.cloud.architect.groot.request.ListChangeRequest;
import com.choice.cloud.architect.groot.request.UpdateChangeRequest;
import com.choice.cloud.architect.groot.request.UpdateChangeStageRequest;
import com.choice.cloud.architect.groot.request.UpdateChangeStatusRequest;
import com.choice.cloud.architect.groot.response.ResponseData;
import com.choice.cloud.architect.groot.response.code.ChangeResponseCode;
import com.choice.cloud.architect.groot.response.code.MilkyWayResponseCode;
import com.choice.cloud.architect.groot.response.code.SysResponseCode;
import com.choice.cloud.architect.groot.service.inner.GrChangeService;
import com.choice.cloud.architect.groot.util.CommonUtil;
import com.choice.cloud.architect.groot.util.UserHolder;
import com.choice.driver.jwt.entity.AuthUser;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.choice.cloud.architect.groot.enums.EnvTypeEnum.pre;
import static com.choice.cloud.architect.groot.enums.EnvTypeEnum.pro;

/**
 * @author zhangkun
 */
@Service
@Slf4j
public class GrChangeServiceImpl extends BaseService implements GrChangeService {
    @Autowired
    private GrChangeMapper grChangeMapper;

    @Autowired
    private ChangeConvertor changeConvertor;

    @Autowired
    private GrMemberMapper grMemberMapper;

    @Autowired
    private GrPublishMapper grPublishMapper;

    @Autowired
    private GrChangeLifeCircleMapper grChangeLifeCircleMapper;

    @Autowired
    private MilkywayService milkywayService;

    @Autowired
    private GitlabService gitlabService;

    @Autowired
    private NoPreAppWhiteListProperties noPreAppWhiteListProperties;

    @Autowired
    private MessageCenterProcessFeignClient messageCenterProcessFeignClient;

    @Value("${publish.sendnotice.flag}")
    private String sendNoticeFlag;

    @Value("${publish.sendnotice.three.phase.users}")
    private String threePhaseUserIds;

    @Autowired
    private MqSendNoticeService mqSendNoticeService;

    @Autowired
    private MilkywayClient milkywayClient;

    @Value("${client.perpetual.token}")
    private String token;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String createChange(CreateChangeRequest createChangeRequest, AuthUser authUser) {
        if (createChangeRequest == null) {
            throw new ServiceException(SysResponseCode.PARAM_ILLEGAL);
        }
        AppListResponse appInfo = milkywayService.getAppInfo(createChangeRequest.getAppCode());
        if (appInfo == null) {
            throw new ServiceException(MilkyWayResponseCode.NO_APPINFO_OF_MILKYWAY);
        }
        if (BranchChangeEnum.CREATE.getCode().equals(createChangeRequest.getBranchChange())
                && createChangeRequest.getBranchName() != null) { // 如果是创建分支
            gitlabService.createBranchRetMaster(appInfo.getGitAddress(), createChangeRequest.getBranchName());
        } else if (BranchChangeEnum.SELECT.getCode().equals(createChangeRequest.getBranchChange())
                && createChangeRequest.getBranchName() != null) { // 如果是选择已有分支，判断git仓库中是否真的存在
            boolean containsBranchName = gitlabService.containsBranchName(appInfo.getGitAddress(), createChangeRequest.getBranchName());
            if (!containsBranchName) {
                throw new ServiceException(ChangeResponseCode.NO_HAVE_BRANCH);
            }
        } else {
            throw new ServiceException(SysResponseCode.PARAM_ILLEGAL);
        }
        GrChange grChange = new GrChange();
        BeanUtils.copyProperties(createChangeRequest, grChange);
        initSaveModel(grChange, authUser);
        grChange.setVersion(1);
        grChange.setStatus(ChangeEnum.RUNNING.getCode());
        //约定，阿波罗appId 和服务名ID
        grChange.setApolloAppCode(createChangeRequest.getAppCode());
        grChange.setGitlabAddress(appInfo.getGitAddress());
        // 新建为测试阶段
        grChange.setStage(EnvTypeEnum.test.value());
        grChange.setBranchName(createChangeRequest.getBranchName().trim());
        grChange.setOperatorId(authUser.getUid());
        grChange.setIterationId(ObjectUtils.defaultIfNull(createChangeRequest.getIterationId(), ""));
        grChangeMapper.insert(grChange);
        checkCrMembers(createChangeRequest);
        insertMembers(createChangeRequest, grChange);
        return grChange.getOid();
    }

    @Override
    public WebPage<List<ListChangeDTO>> list(ListChangeRequest listChangeRequest, AuthUser authUser) {
        if (authUser != null) {
            List<GrMember> relationsByMemberId = grMemberMapper.listByMemberId(authUser.getUid(), GlobalConst.NOT_DELETE);
            Set<String> changeIds = relationsByMemberId.stream().map(GrMember::getChangeId).collect(Collectors.toSet());
            listChangeRequest.setIds(Lists.newArrayList(changeIds));
        }

        return list(listChangeRequest);
    }

    @Override
    public WebPage<List<ListChangeDTO>> list(ListChangeRequest listChangeRequest) {
        Integer pageNum = listChangeRequest.getPageNum();
        Integer pageSize = listChangeRequest.getPageSize();
        GrChange grChangeParam = changeConvertor.request2Change(listChangeRequest);
        List<ListChangeDTO> changeDTOList = Lists.newArrayList();

        PageHelper.startPage(pageNum, pageSize);
        grChangeParam.setDeleteFlag(GlobalConst.NOT_DELETE);
        List<GrChange> grChangeList = grChangeMapper.listWithPage(grChangeParam);

        if (CollectionUtils.isEmpty(grChangeList)) {
            return new WebPage<>(pageNum, pageSize, 0, null);
        }

        List<String> changeIdsList = grChangeList.stream().map(GrChange::getOid).collect(Collectors.toList());
        List<GrMember> developerMembers = grMemberMapper.queryByChangeIds(MemberEnum.DEVELOPER.name(),changeIdsList, GlobalConst.NOT_DELETE);
        List<GrMember> connerMembers = grMemberMapper.queryByChangeIds(MemberEnum.CONNER.name(),changeIdsList, GlobalConst.NOT_DELETE);
        List<GrMember> crMembers = grMemberMapper.queryByChangeIds(MemberEnum.CODEREVIEW.name(),changeIdsList, GlobalConst.NOT_DELETE);

        Map<String, List<GrMember>> developerMemberMap = developerMembers.stream().collect(Collectors.groupingBy(GrMember::getChangeId));
        Map<String, List<GrMember>> connerMemberMap = connerMembers.stream().collect(Collectors.groupingBy(GrMember::getChangeId));
        Map<String, List<GrMember>> crMemberMap = crMembers.stream().collect(Collectors.groupingBy(GrMember::getChangeId));

        Map<String, String> conMemberMap = Maps.newHashMap();
        connerMemberMap.forEach((k,v) -> {
            StringBuilder conners = new StringBuilder();
            if(CollectionUtils.isNotEmpty(v)){
                for (GrMember grMember:v) {
                    conners.append(grMember.getMemberName()).append(",");
                }
            }
            conMemberMap.put(k,conners.length()>1?conners.substring(0,conners.length()-1):conners.toString());
        });

        Map<String, String> devMemberMap = Maps.newHashMap();
        developerMemberMap.forEach((k,v) -> {
            StringBuilder developers = new StringBuilder();
            if(CollectionUtils.isNotEmpty(v)){
                for (GrMember grMember:v) {
                    developers.append(grMember.getMemberName()).append(",");
                }
            }
            devMemberMap.put(k,developers.length()>1?developers.substring(0,developers.length()-1):developers.toString());
        });

        for (GrChange grChange : grChangeList) {
            ListChangeDTO changeDTO = changeConvertor.change2Dto(grChange);
            changeDTO.setId(grChange.getId());
            changeDTO.setDeveloper(devMemberMap.get(grChange.getOid()));
            changeDTO.setConner(conMemberMap.get(grChange.getOid()));
            changeDTO.setCrMembers(crMemberMap.get(grChange.getOid()));
            changeDTOList.add(changeDTO);
        }

        PageInfo<GrChange> info = new PageInfo<>(grChangeList);
        return new WebPage<>(pageNum, pageSize, info.getTotal(), changeDTOList);
    }

    @Override
    public ListChangeDTO queryByOid(String oid) {
        GrChange grChange = grChangeMapper.selectById(oid);
        ArrayList<String> list = Lists.newArrayList(oid);
        List<GrMember> developerMembers = grMemberMapper.queryByChangeIds(MemberEnum.DEVELOPER.name(),list, GlobalConst.NOT_DELETE);
        List<GrMember> connerMembers = grMemberMapper.queryByChangeIds(MemberEnum.CONNER.name(), list, GlobalConst.NOT_DELETE);
        List<GrMember> crMembers = grMemberMapper.queryByChangeIds(MemberEnum.CODEREVIEW.name(), list, GlobalConst.NOT_DELETE);
        ListChangeDTO listChangeDTO = changeConvertor.change2Dto(grChange);
        listChangeDTO.setDeveloperMembers(developerMembers);
        listChangeDTO.setConnerMembers(connerMembers);
        listChangeDTO.setCrMembers(crMembers);
        if(CollectionUtils.isNotEmpty(developerMembers)){
            StringBuilder sb = new StringBuilder();
            for (GrMember grMember:developerMembers) {
                sb.append(grMember.getMemberName()).append(",");
            }
            listChangeDTO.setDeveloper(sb.substring(0,sb.length()-1));
        }
        if(CollectionUtils.isNotEmpty(connerMembers)) {
            StringBuilder sb = new StringBuilder();
            for (GrMember grMember:connerMembers) {
                sb.append(grMember.getMemberName()).append(",");
            }
            listChangeDTO.setConner(sb.substring(0,sb.length()-1));
        }
        return listChangeDTO;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateChange(UpdateChangeRequest updateChangeRequest, AuthUser authUser) {
        GrChange grChange = new GrChange();
        grChange.setOid(updateChangeRequest.getOid());
        grChange.setDeleteBranch(updateChangeRequest.getDeleteBranch());
        grChange.setPublishTime(updateChangeRequest.getPublishTime());
        grChange.setUpdateTime(LocalDateTime.now());
        grChange.setUpdateUser(UserHolder.getUserName(authUser.getUid()));
        grChangeMapper.updateDynamic(grChange);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateStatusChange(UpdateChangeStatusRequest updateChangeStatusRequest, AuthUser authUser) {
        GrChange grChange = new GrChange();
        grChange.setStatus(updateChangeStatusRequest.getStatus());
        grChange.setOid(updateChangeStatusRequest.getOid());
        grChange.setUpdateUser(UserHolder.getUserName(authUser.getUid()));
        grChange.setUpdateTime(LocalDateTime.now());

        GrChange dbGrChange = grChangeMapper.selectById(updateChangeStatusRequest.getOid());
        if(ChangeEnum.PUBLISHING.getCode().equals(updateChangeStatusRequest.getStatus())){
            if(!ChangeEnum.RUNNING.getCode().equals(dbGrChange.getStatus())){
                throw new ServiceException(ChangeResponseCode.STATUS_PUBLISHING_ERROR);
            }
        }
        if(ChangeEnum.TESTED.getCode().equals(updateChangeStatusRequest.getStatus())){
            if(!ChangeEnum.PUBLISHING.getCode().equals(dbGrChange.getStatus())){
                throw new ServiceException(ChangeResponseCode.STATUS_PUBLISH_ERROR);
            }
        }

        grChangeMapper.updateDynamic(grChange);
    }

    @Override
    public void updateChangeStage(UpdateChangeStageRequest updateChangeStageRequest, String userName) {
        GrChange grChange = new GrChange();
        grChange.setStage(updateChangeStageRequest.getStage());
        grChange.setOid(updateChangeStageRequest.getOid());
        grChange.setStatus(updateChangeStageRequest.getStatus());
        grChange.setUpdateUser(userName);
        grChange.setUpdateTime(LocalDateTime.now());
        grChangeMapper.updateChangeStage(grChange);
    }

    /**
     * 验收变更单，修改状态
     * 包括验收通过和验收不通过两个操作
     * @param updateChangeStatusRequest
     * @param userName
     */
    @Override
    public void updateChangeTestStatus(UpdateChangeStatusRequest updateChangeStatusRequest, String userName) {
        Integer status = updateChangeStatusRequest.getStatus();
        if (!status.equals(ChangeEnum.TESTED.getCode()) && !status.equals(ChangeEnum.TEST_FAIL.getCode())) {
            throw new ServiceException(SysResponseCode.PARAM_ILLEGAL);
        }

        GrChange grChange = grChangeMapper.selectById(updateChangeStatusRequest.getOid());
        // 变更单老的所处阶段
        String oldStage = grChange.getStage();
        String newStage = null;
        Integer newStatus = null;

        AppListResponse app = milkywayService.getAppInfo(grChange.getAppCode());
        if (null == app) {
            throw new ServiceException("应用npe");
        }

        // 验收通过
        if (status.equals(ChangeEnum.TESTED.getCode())) {
            if (oldStage.equalsIgnoreCase(EnvTypeEnum.test.value())) {
                newStage = EnvTypeEnum.pre.value();
            } else if (oldStage.equalsIgnoreCase(EnvTypeEnum.pre.value())) {
                newStage = pro.value();
                // 非空发版并且没有配置无预发白名单的应用执行
                boolean checkPreFlag = this.isCheckPre(app);
                if (checkPreFlag) {
                    GrPublish lastPrePublish = grPublishMapper.lastByAppEnv(grChange.getAppCode(), pro.value(), pre.value(), GlobalConst.NOT_DELETE);

                    if (lastPrePublish == null) {
                        throw new ServiceException(ChangeResponseCode.PRE_TEST_NOT_PUBLISH_ERROR);
                    }

                    // pre最后一次发布不是成功状态，抛出异常
                    if (!lastPrePublish.getPublishStatus().equalsIgnoreCase(PublishStatusEnum.SUCCESS.value())) {
                        throw new ServiceException(ChangeResponseCode.PRE_TEST_ERROR);
                    }
                }
            } else if (oldStage.equalsIgnoreCase(pro.value())) {
                // 测试通过,且为线上分支,且当前分支不是master，合并代码
                if(!(FrameWorkEnum.FW_JAR.getCode().equalsIgnoreCase(app.getFramework())
                    || FrameWorkEnum.FW_NULL_PUBLISH.getCode().equalsIgnoreCase(app.getFramework()))) {
                    gitlabService.merge(grChange.getGitlabAddress(), grChange.getBranchName(), "master", "发布到生产环境验证通过，合并到master，变更名称：" + grChange.getChangeName());
                }
                log.info("合并分支 gitRepo:{},gitBranch:{}", grChange.getGitlabAddress(), grChange.getBranchName());

                // 20200908线上环境验收通过之后，修改变更单状态为已验收通过
                newStatus = ChangeEnum.TESTED.getCode();
            }
        } else if(status.equals(ChangeEnum.TEST_FAIL.getCode()) && oldStage.equalsIgnoreCase(pro.value())) {
            // 20200908线上环境验收未通过之后，修改变更单状态为验收未通过待重发
            newStatus = ChangeEnum.TEST_FAIL.getCode();
        }
        GrChange updateChange = new GrChange();
        updateChange.setStage(newStage);
        updateChange.setStatus(newStatus);
        updateChange.setOid(updateChangeStatusRequest.getOid());
        updateChange.setUpdateUser(userName);
        updateChange.setUpdateTime(LocalDateTime.now());
        grChangeMapper.updateDynamic(updateChange);

        GrChangeLifeCircle grChangeLifeCircle = new GrChangeLifeCircle();
        grChangeLifeCircle.setChangeId(updateChangeStatusRequest.getOid());
        grChangeLifeCircle.setEnvType(oldStage);
        grChangeLifeCircle.setAppCode(grChange.getAppCode());
        grChangeLifeCircle.setChangeName(grChange.getChangeName());
//        grChangeLifeCircle.setEnvCode();
        grChangeLifeCircle.setEnvName("无");
        grChangeLifeCircle.setOperateTime(LocalDateTime.now());
        grChangeLifeCircle.setOperateResult(
                status.equals(ChangeEnum.TESTED.getCode())?
                OperateResultEnum.SUCCESS.value():
                OperateResultEnum.FAILURE.value()
            );
        grChangeLifeCircle.setOperator(userName);
        grChangeLifeCircle.setProject(grChange.getBelongProject());
        grChangeLifeCircle.setRelOrderType(RelOrderTypeEnum.ORDER_CHANGE.value());
        grChangeLifeCircle.setRelOrderId(grChange.getOid());
        grChangeLifeCircle.setOperateType(OperateTypeEnum.TEST.value());
        grChangeLifeCircle.setOid(CommonUtil.getUUIdUseMongo());
        grChangeLifeCircle.setDeleteFlag(GlobalConst.NOT_DELETE);
        grChangeLifeCircle.setCreateTime(LocalDateTime.now());
        grChangeLifeCircleMapper.insert(grChangeLifeCircle);
    }

    /**
     * 是否校验预发限制 true校验  false不校验
     * 非空发版并且没有配置无预发白名单的应用执行
     * @param app
     * @return
     */
    private boolean isCheckPre(AppListResponse app) {
        if (null == app) {
            throw new ServiceException("应用npe");
        }
        // 空发版应用无需进行预发校验
        // 白名单中的应用无需进行预发校验
        boolean checkPreFlag = true;
        List<String> noPreAppList = noPreAppWhiteListProperties.getList();
        if (CollectionUtils.isNotEmpty(noPreAppList) && StringUtils.isNotBlank(app.getAppCode())
            && noPreAppList.contains(app.getAppCode())) {
            checkPreFlag = false;
        }
        return (!FrameWorkEnum.FW_NULL_PUBLISH.getCode().equalsIgnoreCase(app.getFramework())) && checkPreFlag;
    }

    @Override
    public boolean existsChange(String id) {
        GrChange grChange = grChangeMapper.selectById(id);

        if (null != grChange) {
            return true;
        }

        return false;
    }

    private List<AppConfigRelationDTO> getConfigList(AppListResponse appListResponse){
        List<AppConfigRelationDTO> configOptionList = appListResponse.getConfigOptionList();
        if(CollectionUtils.isEmpty(configOptionList)){
            throw new ServiceException(MilkyWayResponseCode.NO_CONFIGS_OF_APOLLO);
        }
        return configOptionList;
    }

    private String getApolloAppId(List<AppConfigRelationDTO> configList){
        Map<String, List<AppConfigRelationDTO>> configMap = configList.stream().collect(Collectors.groupingBy(AppConfigRelationDTO::getConfigName));
        List<AppConfigRelationDTO> apolloAppId = configMap.get("apolloAppId");
        if(CollectionUtils.isEmpty(apolloAppId)){
            throw new ServiceException(MilkyWayResponseCode.NO_MAPPING_OF_APOLLO_APPID);
        }
        return apolloAppId.get(0).getValue();
    }

    private void checkCrMembers(CreateChangeRequest createChangeRequest) {
        List<CreateMemberRequest> members = createChangeRequest.getMembers();
        Map<String, List<CreateMemberRequest>> membersMap = members.stream()
                .collect(Collectors.groupingBy(CreateMemberRequest::getMemberType));

        List<CreateMemberRequest> crMembers = membersMap.get(MemberEnum.CODEREVIEW.name());
        List<CreateMemberRequest> devMembers = membersMap.get(MemberEnum.DEVELOPER.name());
        if (CollectionUtils.isNotEmpty(crMembers) && CollectionUtils.isNotEmpty(devMembers)) {
            if (crMembers.size() == 1) {
                if(devMembers.stream().anyMatch(m -> m.getMemberId().equals(crMembers.get(0).getMemberId()))) {
                    throw new ServiceException(ChangeResponseCode.CR_DEV_MEMBER_SAME);
                }
            }
        }

    }

    public void insertMembers(CreateChangeRequest createChangeRequest,GrChange grChange){

        List<GrMember> memberList = Lists.newArrayList();
        List<CreateMemberRequest> members = createChangeRequest.getMembers();
        for (CreateMemberRequest member : members) {
            GrMember build = GrMember.builder().build();
            BeanUtils.copyProperties(member,build);
            build.setOid(CommonUtil.getUUIdUseMongo());
            build.setChangeId(grChange.getOid());
            build.setCreateUser(grChange.getCreateUser());
            build.setCreateTime(LocalDateTime.now());
            build.setDeleteFlag(GlobalConst.NOT_DELETE);
            build.setStatus(GlobalConst.ENABLE);
            memberList.add(build);
        }

        grMemberMapper.batchInsert(memberList);
    }

    @Override
    public List<GrChange> queryByIds(Set<String> changeIds) {
        return grChangeMapper.selectByIds(Lists.newArrayList(changeIds));
    }

    @Override
    public List<GrChange> queryByDo(GrChange grChangeDo) {
        return grChangeMapper.list(grChangeDo);
    }

    /**
     * 针对变更单发送钉钉工作通知
     *
     * @param sendNoticeDTO
     * @return
     */
    @Override
    public boolean handleChangeSendWebhookMessage(PublishSendNoticeDTO sendNoticeDTO) {
        log.info("[针对变更单发送钉钉工作通知]接收到的消息体：{}", JSON.toJSONString(sendNoticeDTO));
        if (Objects.isNull(sendNoticeDTO)) {
            log.error("[变更单发送钉钉工作通知]消息体为空");
            return false;
        }
        if (StringUtils.isBlank(sendNoticeDTO.getChangeId())) {
            log.error("[变更单发送钉钉工作通知]变更单id为空");
            return false;
        }
        // 根据变更单id查询对应的变更单信息
        GrChange grChange = grChangeMapper.selectById(sendNoticeDTO.getChangeId());
        if (Objects.isNull(grChange)) {
            log.error("[变更单发送钉钉工作通知]根据变更单id没有查询到对应的变更单");
            return false;
        }
        boolean checkFlag = this.checkIsSendNotice(grChange);
        log.info("是否发送工作通知开关，checkFlag：{}", checkFlag);
        log.info("配置的发送工作通知开关，sendNoticeFlag：{}", sendNoticeFlag);
        if (checkFlag && StringUtils.isNotBlank(sendNoticeFlag) && "on".equalsIgnoreCase(sendNoticeFlag)) {
            // 组装发送工作通知请求体
            OapiMessageCorpconversationAsyncsendV2Request request = this.assembleNotice(grChange, sendNoticeDTO);
            if (Objects.isNull(request)) {
                log.info("[变更单发布成功未验收]，发起钉钉工作通知整个流程已结束");
                return true;
            }
            log.info("[变更单发布成功未验收]，发起钉钉工作通知内容：{}", JSON.toJSONString(request));
            ResponseData<String> responseData = messageCenterProcessFeignClient.sendCorporationNotice(request);
            log.info("[变更单发布成功未验收]，发起钉钉工作通知调用结果：{}", responseData.getMsg());

            // 获取下一阶段
            String nextPhase = PublishSendNoticePhaseEnum.getNextPhase(sendNoticeDTO.getPhaseCode());
            // 下一阶段为空，说明已走完全部通知阶段流程
            if (StringUtils.isBlank(nextPhase)) {
                log.info("获取下一阶段时为空，说明已走完全部通知阶段流程，不再发送延迟消息");
                return true;
            }
            // 发送延迟消息
            // 验收完成请点击验收通过, 发布完成未点击验收通过，3h通知应用owner+应用发布人，6h通知架构域，10h冲哥
            PublishSendNoticeDTO message = this.assembleNextMessage(sendNoticeDTO, grChange.getAppCode(), nextPhase);
            log.info("[变更单发布成功未验收]发送参数，message：{}， nextPhase：{}", JSON.toJSONString(message), nextPhase);
            SendResult sendResult = mqSendNoticeService.sendNoticeMsg(JSON.toJSONString(message), nextPhase);
            log.info("[变更单发布成功未验收]发送结果，sendResult：{}", JSON.toJSONString(sendResult));
            return true;
        }
        return true;
    }

    private PublishSendNoticeDTO assembleNextMessage(PublishSendNoticeDTO sendNoticeDTO, String appCode, String nextPhase) {
        PublishSendNoticeDTO message = new PublishSendNoticeDTO();
        message.setChangeId(sendNoticeDTO.getChangeId());
        message.setPhaseCode(nextPhase);
        message.setPublishUserId(sendNoticeDTO.getPublishUserId());
        // 根据应用code查询应用信息
        ApplicationParamAppCode appParam = new ApplicationParamAppCode();
        appParam.setAppCode(appCode);
        ResponseData<ApplicationDTO> appResponse = milkywayClient.getApplicationInfoByAppCode(token, appParam);
        ApplicationDTO appDTO = new ApplicationDTO();
        if (appResponse.isSuccess() && null != appResponse.getData()) {
            appDTO = appResponse.getData();
        }
        message.setOwnerId(appDTO.getOwnerId());
        // 根据架构域id查询架构域信息
        DomainArchitectureParamId domainParam = new DomainArchitectureParamId();
        domainParam.setOid(appDTO.getDomainArchitectureId());
        ResponseData<DomainArchitectureDTO> domainArchitectureResponse = milkywayClient.getDomainArchitectureInfoByOid(token, domainParam);
        DomainArchitectureDTO domainArchitectureDTO = new DomainArchitectureDTO();
        if (domainArchitectureResponse.isSuccess() && null != domainArchitectureResponse.getData()) {
            domainArchitectureDTO = domainArchitectureResponse.getData();
        }
        message.setDomainOwnerId(domainArchitectureDTO.getOwnerId());
        return message;
    }

    /**
     * 判断是否发送工作通知
     * @param grChange
     * @return
     */
    private boolean checkIsSendNotice(GrChange grChange) {
        if (Objects.isNull(grChange)) {
            log.error("变更单为空");
            return false;
        }
        if (!(ChangeEnum.TESTED.getCode().equals(grChange.getStatus())
            || ChangeEnum.TEST_FAIL.getCode().equals(grChange.getStatus()))) {
            return true;
        }
        return false;
    }

    private OapiMessageCorpconversationAsyncsendV2Request assembleNotice(GrChange grChange, PublishSendNoticeDTO sendNoticeDTO) {
        OapiMessageCorpconversationAsyncsendV2Request request = new OapiMessageCorpconversationAsyncsendV2Request();
        if (Objects.isNull(sendNoticeDTO)) {
            log.info("发送工作通知时消息体为空");
            return null;
        }
        String userIdList;
        if (PublishSendNoticePhaseEnum.ONE_PHASE.getCode().equalsIgnoreCase(sendNoticeDTO.getPhaseCode())) {
            // 查询对应的发起人和业务owner
            if (StringUtils.isBlank(sendNoticeDTO.getPublishUserId()) && StringUtils.isBlank(sendNoticeDTO.getOwnerId())) {
                log.info("发送工作通知时一阶段人员都没有找到");
                return null;
            } else if (StringUtils.isBlank(sendNoticeDTO.getPublishUserId())) {
                userIdList = sendNoticeDTO.getOwnerId();
            } else if (StringUtils.isBlank(sendNoticeDTO.getOwnerId())) {
                userIdList = sendNoticeDTO.getPublishUserId();
            } else {
                userIdList = sendNoticeDTO.getPublishUserId() + "," + sendNoticeDTO.getOwnerId();
            }
        } else if (PublishSendNoticePhaseEnum.TWO_PHASE.getCode().equalsIgnoreCase(sendNoticeDTO.getPhaseCode())) {
            // 查询对应的架构域owner
            if (StringUtils.isBlank(sendNoticeDTO.getDomainOwnerId())) {
                log.info("发送工作通知时二阶段人员没有找到");
                return null;
            }
            userIdList = sendNoticeDTO.getDomainOwnerId();
        } else if (PublishSendNoticePhaseEnum.THREE_PHASE.getCode().equalsIgnoreCase(sendNoticeDTO.getPhaseCode())) {
            // 发送周冲
            if (StringUtils.isBlank(threePhaseUserIds)) {
                log.info("发送工作通知时三阶段人员没有找到");
                return null;
            }
            userIdList = threePhaseUserIds;
        } else {
            log.info("发送工作通知时消息体对应的阶段不正确");
            return null;
        }
        request.setUseridList(userIdList);
        OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
        msg.setMsgtype("markdown");
        msg.setMarkdown(new OapiMessageCorpconversationAsyncsendV2Request.Markdown());
        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append("**应用名称**：").append(grChange.getAppName()).append("  \n  ");
        if (PublishSendNoticePhaseEnum.THREE_PHASE.getCode().equalsIgnoreCase(sendNoticeDTO.getPhaseCode())) {
            contentBuilder.append("**特别注意**：").append("该变更单已发布线上成功，却未验收！故升级到此！\n").append("  \n  ");
        } else {
            contentBuilder.append("**特别注意**：").append("该变更单已发布线上成功，却未验收！  \n  请尽快去验收，否则将会升级！\n").append("  \n  ");
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        contentBuilder.append("**通知时间**：").append(LocalDateTime.now().format(dateTimeFormatter)).append("  \n  ");
        msg.getMarkdown().setText(contentBuilder.toString());
        msg.getMarkdown().setTitle("Groot变更单发布成功未验收通知");
        request.setMsg(msg);

        return request;
    }
}
