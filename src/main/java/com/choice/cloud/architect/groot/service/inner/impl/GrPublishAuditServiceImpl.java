package com.choice.cloud.architect.groot.service.inner.impl;

import com.alibaba.fastjson.JSON;
import com.choice.cloud.architect.groot.convertor.PublishAuditConvertor;
import com.choice.cloud.architect.groot.dao.GrPublishAuditChangeRelMapper;
import com.choice.cloud.architect.groot.dao.GrPublishAuditMapper;
import com.choice.cloud.architect.groot.dao.GrPublishAuditRecordMapper;
import com.choice.cloud.architect.groot.dataobject.PublishAuditQueryPageDO;
import com.choice.cloud.architect.groot.dto.*;
import com.choice.cloud.architect.groot.dto.AuditDTalkProcessListResponseDTO.OperationRecordDTO;
import com.choice.cloud.architect.groot.dto.AuditDTalkProcessListResponseDTO.TaskDTO;
import com.choice.cloud.architect.groot.enums.EnvTypeEnum;
import com.choice.cloud.architect.groot.enums.PublishAuditEnum;
import com.choice.cloud.architect.groot.enums.PublishStatusEnum;
import com.choice.cloud.architect.groot.enums.PublishTypeEnum;
import com.choice.cloud.architect.groot.exception.ServiceException;
import com.choice.cloud.architect.groot.facade.GrChangeFacade;
import com.choice.cloud.architect.groot.facade.GrPublishFacade;
import com.choice.cloud.architect.groot.model.*;
import com.choice.cloud.architect.groot.option.GlobalConst;
import com.choice.cloud.architect.groot.process.config.ProcessConfig;
import com.choice.cloud.architect.groot.process.dto.ProcessInfoQueryDTO;
import com.choice.cloud.architect.groot.process.enums.*;
import com.choice.cloud.architect.groot.process.service.MessageCenterProcessFeignClient;
import com.choice.cloud.architect.groot.process.service.ProcessService;
import com.choice.cloud.architect.groot.remote.AppListResponse;
import com.choice.cloud.architect.groot.remote.AppRoleRelationDTO;
import com.choice.cloud.architect.groot.remote.DomainArchitectureDTO;
import com.choice.cloud.architect.groot.remote.enums.RoleEnum;
import com.choice.cloud.architect.groot.remote.milkyway.MilkywayService;
import com.choice.cloud.architect.groot.remote.usercenter.UserCenterClient;
import com.choice.cloud.architect.groot.remote.usercenter.UserDTO;
import com.choice.cloud.architect.groot.request.CreatePublishAuditRequest;
import com.choice.cloud.architect.groot.request.EditPublishAuditRequest;
import com.choice.cloud.architect.groot.request.ListPublishAuditRequest;
import com.choice.cloud.architect.groot.response.Page;
import com.choice.cloud.architect.groot.response.ResponseData;
import com.choice.cloud.architect.groot.response.code.PublishAuditResponseCode;
import com.choice.cloud.architect.groot.response.code.PublishResponseCode;
import com.choice.cloud.architect.groot.service.inner.GrPublishAuditService;
import com.choice.cloud.architect.groot.service.inner.PublishBlackListService;
import com.choice.cloud.architect.groot.util.CommonUtil;
import com.choice.cloud.architect.groot.util.DateUtil;
import com.choice.driver.jwt.entity.AuthUser;
import com.dingtalk.api.request.OapiProcessinstanceCreateRequest;
import com.dingtalk.api.request.OapiProcessinstanceCreateRequest.FormComponentValueVo;
import com.dingtalk.api.request.OapiProcessinstanceCreateRequest.ProcessInstanceApproverVo;
import com.dingtalk.api.response.OapiProcessinstanceGetResponse.OperationRecordsVo;
import com.dingtalk.api.response.OapiProcessinstanceGetResponse.ProcessInstanceTopVo;
import com.dingtalk.api.response.OapiProcessinstanceGetResponse.TaskTopVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhangkun
 */
@Slf4j
@Service
public class GrPublishAuditServiceImpl extends BaseService implements GrPublishAuditService {

    @Autowired
    private GrPublishAuditMapper grPublishAuditMapper;

    @Autowired
    private GrPublishAuditChangeRelMapper grPublishAuditChangeRelMapper;

    @Autowired
    private GrPublishAuditRecordMapper grPublishAuditRecordMapper;

    @Autowired
    private GrChangeFacade grChangeFacade;

    @Autowired
    private GrPublishFacade grPublishFacade;

    @Autowired
    private ProcessService processService;

    @Autowired
    private MilkywayService milkywayService;

    @Autowired
    private PublishBlackListService publishBlackListService;

    @Resource
    private ProcessConfig processConfig;

    @Resource
    private MessageCenterProcessFeignClient messageCenterProcessFeignClient;

    @Resource
    private UserCenterClient userCenterClient;

    @Value("${client.perpetual.token}")
    private String token;

    @Value("${develop.delay.time}")
    private Integer developTime;

    @Value("${daily.delay.time}")
    private Integer dailyTime;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GrPublishAudit createPublishAudit(CreatePublishAuditRequest request, AuthUser authUser) {
        if (CollectionUtils.isEmpty(request.getChangeIds())) {
            throw new ServiceException("??????????????????????????????");
        }

        if(request.getEnvType().equalsIgnoreCase(EnvTypeEnum.pro.value()) && request.getEnvCode().equalsIgnoreCase("stable")) {
            if (StringUtils.isBlank(request.getPublishApplyUrl())) {
                throw new ServiceException("????????????????????????");
            }

            if (!request.getPublishApplyUrl().startsWith("https://choicesoft.yuque.com/")) {
                throw new ServiceException("?????????????????????");
            }
        }

        GrPublishAudit grPublishAudit = new GrPublishAudit();
        grPublishAudit = PublishAuditConvertor.INSTANCE.convert(request);
        grPublishAudit.setAuditStatus(PublishAuditEnum.TO_BE_COMMITTED.value());
        initSaveModel(grPublishAudit, authUser);
        grPublishAudit.setInitiatorId(authUser.getUid());
        grPublishAudit.setInitiatorName(currentUserName(authUser.getUid()));
        grPublishAudit.setPublishApplyUrl(request.getPublishApplyUrl());
        grPublishAudit.setDbChangeFlag(request.getDbChangeFlag());

        grPublishAuditMapper.insert(grPublishAudit);

        grChangeFacade.linkPublishAudit(grPublishAudit.getOid(), request.getChangeIds(), authUser);

        return grPublishAudit;
    }

    @Override
    public ResponseData<List<ListPublishAuditDTO>> pageQueryPublishAudit(ListPublishAuditRequest request) {
        Integer pageNum = request.getPageNum();
        Integer pageSize = request.getPageSize();

        List<ListPublishAuditDTO> listPublishAuditDTOList = Lists.newArrayList();
        PageHelper.startPage(pageNum, pageSize);
        PublishAuditQueryPageDO publishAuditQueryPageDO = new PublishAuditQueryPageDO();
        publishAuditQueryPageDO.setStatus(request.getStatus());
        publishAuditQueryPageDO.setEnvType(EnvTypeEnum.pro.value());
        publishAuditQueryPageDO.setSearchValue(request.getSearchValue());
        List<GrPublishAudit> publishAuditList = grPublishAuditMapper.pageList(publishAuditQueryPageDO);
        if (CollectionUtils.isEmpty(publishAuditList)) {
            return ResponseData.createBySuccess();
        }

        for (GrPublishAudit grPublishAudit : publishAuditList) {
            ListPublishAuditDTO listPublishAuditDTO = PublishAuditConvertor.INSTANCE.convert(grPublishAudit);
            listPublishAuditDTO.setPublishType(PublishTypeEnum.description(grPublishAudit.getPublishType()));
            listPublishAuditDTO.setAuditStatus(PublishAuditEnum.description(grPublishAudit.getAuditStatus()));
            List<ChangeOfPublishAuditDTO> changeOfPublishAuditDTOList = grChangeFacade.queryChangeByPublishAuditId(grPublishAudit.getOid());
            List<String> changedAppCodeList = changeOfPublishAuditDTOList.stream()
                    .map(ChangeOfPublishAuditDTO::getAppCode)
                    .collect(Collectors.toList());
            String changedAppCode = Joiner.on("\n").join(changedAppCodeList);
            listPublishAuditDTO.setPublishApps(changedAppCode);

            /**
             * ??????dTalkId
             */
            listPublishAuditDTO.setDTalkProcessId(grPublishAudit.getDtalkProcessId());

            listPublishAuditDTOList.add(listPublishAuditDTO);
        }

        PageInfo<GrPublishAudit> pageInfo = new PageInfo<>(publishAuditList);
        Page page = new Page();
        page.setPageNum(pageNum);
        page.setPageSize(pageSize);
        page.setTotal((int) pageInfo.getTotal());

        return ResponseData.createBySuccess(page, listPublishAuditDTOList);
    }

    @Override
    public PublishAuditDetailDTO getPublishAuditDetail(String oid) {
        GrPublishAudit grPublishAudit = grPublishAuditMapper.load(oid);

        PublishAuditDetailDTO publishAuditDetailDTO = PublishAuditConvertor.INSTANCE.convertDetail(grPublishAudit);
        List<ChangeOfPublishAuditDTO> changeOfPublishAuditDTOList = grChangeFacade.queryChangeByPublishAuditId(oid);
        publishAuditDetailDTO.setChangeDetails(changeOfPublishAuditDTOList);

        return publishAuditDetailDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitPublishAudit(String oid, AuthUser authUser) {
        log.info("GrPublishAuditServiceImpl submitPublishAudit ??????????????? oid:{}", oid);
        // 0.
        GrPublishAudit grPublishAudit = grPublishAuditMapper.load(oid);
        if (!PublishAuditEnum.TO_BE_COMMITTED.value().equals(grPublishAudit.getAuditStatus())
                && !PublishAuditEnum.REJECTED.value().equals(grPublishAudit.getAuditStatus())) {
            throw new ServiceException("????????????????????????????????????????????????????????????");
        }

        if (grPublishAudit.getEnvType().equalsIgnoreCase(EnvTypeEnum.pro.value())
                && grPublishAudit.getEnvCode().equals("stable")) {

            LocalDateTime now = LocalDateTime.now();
            if (!grPublishAudit.getPublishType().equals(PublishTypeEnum.FAULT.value()))  {
                // ?????????????????????????????????????????????
                if (publishBlackListService.publishTimeIsInBlackList(grPublishAudit.getExpectPublishTime())) {
                    throw new ServiceException(PublishResponseCode.PUBLISH_TIME_ILLEGAL);
                }
            }

            // ?????????????????????12????????????????????????12???????????????????????????????????????
            int leadTime = -1;
            if (grPublishAudit.getPublishType().equals(PublishTypeEnum.DAILY.value()))  {
                leadTime = Objects.isNull(dailyTime) ? 12 : dailyTime;
            } else if( grPublishAudit.getPublishType().equals(PublishTypeEnum.DEVELOP.value())) {
                leadTime = Objects.isNull(developTime) ? 3 : developTime;
            }

            if (leadTime > 0 && grPublishAudit.getExpectPublishTime().isBefore(now.plusHours(leadTime))) {
                throw new ServiceException(PublishResponseCode.DAILY_PUBLISH_TIME_ILLEGAL);
            }
        }

        grPublishAudit.setUpdateUser(currentUserName(authUser));
        grPublishAudit.setUpdateTime(LocalDateTime.now());
        log.info("GrPublishAuditServiceImpl submitPublishAudit ??????????????? grPublishAudit:{}", grPublishAudit);

        String dingTalkProcessId = "1";
        String auditStatus = PublishAuditEnum.APPROVED.value();
        // ?????????????????????
        if (EnvTypeEnum.pro.value().equals(grPublishAudit.getEnvType())) { // 1.?????????????????????????????????
            auditStatus = PublishAuditEnum.PENDING_APPROVAL.value();
            dingTalkProcessId = createProcess(grPublishAudit, authUser);
        } else { // ????????????????????????????????????
            this.createPublish(grPublishAudit);
        }
        // 3.??????????????????id??????????????????????????? ?????????????????? -> ???????????????
        GrPublishAudit updateAudit = new GrPublishAudit();
        updateAudit.setOid(grPublishAudit.getOid());
        updateAudit.setDtalkProcessId(dingTalkProcessId);
        updateAudit.setUpdateUser(currentUserName(authUser));
        updateAudit.setAuditStatus(auditStatus);
        grPublishAuditMapper.update(updateAudit);
        log.info("GrPublishAuditServiceImpl submitPublishAudit ???????????????id,????????????????????? oid:{},dingTalkProcessId:{}", grPublishAudit.getOid(), dingTalkProcessId);

        // 4.????????????
        GrPublishAuditRecord grPublishAuditRecord = buildPublishAuditRecord(grPublishAudit, dingTalkProcessId, auditStatus, authUser);
        grPublishAuditRecordMapper.insert(grPublishAuditRecord);
        log.info("GrPublishAuditServiceImpl submitPublishAudit ??????????????????????????? oid:{}", grPublishAudit.getOid());
    }

    /**
     * ???????????????
     * @param grPublishAudit
     * @return
     */
    private String createProcess(GrPublishAudit grPublishAudit, AuthUser authUser) {
        OapiProcessinstanceCreateRequest request = createDingTalkProcessRequest(grPublishAudit, authUser);
        log.info("GrPublishAuditServiceImpl createProcess ???????????????id processRequest:{}", JSON.toJSON(request));
        // ??????message center
        String processInstanceId = processService.createPublishAuditProcess(request);
        log.info("GrPublishAuditServiceImpl createProcess ???????????????id processInstanceId:{}", processInstanceId);
        return processInstanceId;
    }

    private OapiProcessinstanceCreateRequest createDingTalkProcessRequest(GrPublishAudit grPublishAudit, AuthUser authUser) {

        if (grPublishAudit == null) {
            throw new ServiceException("?????????????????????????????????????????????");
        }
        List<ChangeOfPublishAuditDTO> changeDTOList = grChangeFacade.queryChangeListByAuditId(grPublishAudit.getOid());
        if (changeDTOList == null || changeDTOList.size() < 1) {
            throw new ServiceException("?????????????????????????????????");
        }
        String publishTypeDesc = PublishTypeEnum.description(grPublishAudit.getPublishType());
        StringBuilder content = new StringBuilder();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        content.append("???????????????").append(grPublishAudit.getExpectPublishTime().format(dateTimeFormatter)).append("\n");
        content.append("???????????????").append(publishTypeDesc).append("\n");
        content.append("?????????????????????").append(grPublishAudit.getPublishDesc()).append("\n");
        content.append("?????????????????????").append(grPublishAudit.getPublishApplyUrl()).append("\n");
        int index = 0;
        Set<String> appOwnerUserIds = Sets.newHashSet();
        Set<String> domainOwner = Sets.newHashSet();
        Set<String> domainQa = Sets.newHashSet();
        Set<String> appCodes = Sets.newHashSet();
        Set<String> crMemberIds = Sets.newHashSet();
        for (ChangeOfPublishAuditDTO changeDTO : changeDTOList) {
            AppListResponse appInfo = milkywayService.getAppInfo(changeDTO.getAppCode());
            appCodes.add(changeDTO.getAppCode());
            if (appInfo == null) {
                log.error("GrPublishAuditServiceImpl startProcess ??????????????????????????????????????? appCode:{}", changeDTO.getAppCode());
                continue;
            }

            content.append("?????????").append(++index).append("\n")
                    .append("???????????????").append(changeDTO.getAppCode()).append("\n")
                    .append("???????????????").append(changeDTO.getChangeName()).append("\n")
                    .append("???????????????").append(changeDTO.getDescription()).append("\n")
                    .append("????????????????????????").append(changeDTO.getDeveloper()).append("\n")
                    .append("????????????????????????").append(changeDTO.getTester()).append("\n")
                    .append("??????????????????").append(appInfo.getOwnerName()).append("\n");

            if (CollectionUtils.isNotEmpty(changeDTO.getCrMembers())) {
                List<GrMember> changeCrMembers = changeDTO.getCrMembers();
                log.info("??????CR?????????changeId={}, changeName={}, members={}", changeDTO.getOid(), changeDTO.getChangeName(), JSON.toJSON(changeCrMembers));
                String crMemberNames = changeCrMembers.stream()
                        .map(GrMember::getMemberName).collect(Collectors.joining(","));
                content.append("CR?????????").append(crMemberNames).append("\n");
                crMemberIds.addAll(changeCrMembers.stream()
                        .map(GrMember::getMemberId).collect(Collectors.toList()));
            } else {
                throw new ServiceException(PublishAuditResponseCode.CR_NOT_FOUND);
            }

            List<AppRoleRelationDTO> qaTl = appInfo.getMemberMap().get(RoleEnum.QA_TL.getCode());
            if (CollectionUtils.isNotEmpty(qaTl)) {
                content.append("????????????????????????").append(qaTl.get(0).getUserName()).append("\n");
            }

            DomainArchitectureDTO domainArchitectureDTO = milkywayService
                    .getDomainArchitectureInfo(appInfo.getDomainArchitectureId());

            if (StringUtils.isNotBlank(domainArchitectureDTO.getQaName())) {
                content.append("???????????????????????????").append(domainArchitectureDTO.getQaName()).append("\n");
                domainQa.add(domainArchitectureDTO.getQaId());
            }

            if (StringUtils.isNotBlank(domainArchitectureDTO.getOwnerName())) {
                content.append("?????????????????????").append(domainArchitectureDTO.getOwnerName()).append("\n");
                domainOwner.add(domainArchitectureDTO.getOwnerId());
            }

            appOwnerUserIds.add(appInfo.getOwnerId());
        }

        OapiProcessinstanceCreateRequest req = new OapiProcessinstanceCreateRequest();
        req.setProcessCode(processConfig.getProcessCode());
        req.setOriginatorUserId(authUser.getUid());
        req.setDeptId(-1L);

        // ???????????????
        List<ProcessInstanceApproverVo> approverList = Lists.newArrayList();

        Map<String, Boolean> approverMap = Maps.newHashMap();

        // ??????????????????????????????????????????????????????
        if (publishBlackListService.publishTimeIsInBlackList(grPublishAudit.getExpectPublishTime())) {
            // ???????????????
            List<String> finalUserList = Lists.newArrayList(processConfig.getProEmergencyApprover().split(","));
            addProApprover(approverList, finalUserList, approverMap);
        }

        // ??????owner ??????
        addProApprover(approverList, appOwnerUserIds, approverMap);

        PublishTypeEnum publishType = PublishTypeEnum.getEnumByValue(grPublishAudit.getPublishType());

        // ???????????????
        StringBuilder ccBuilder = new StringBuilder(processConfig.getProCc());
        // ??????????????????????????? ???????????????
        if (!grPublishAudit.getEnvCode().equalsIgnoreCase(EnvTypeEnum.pre.value())) {

            // ??????????????????????????????
            List<String> finalTestUserList = Lists.newArrayList(processConfig.getProApprover().split(","));
            // ??????????????????????????????????????????????????????
            List<String> auditUserList = Lists.newArrayList(processConfig.getProAuditApprover().split(","));
            switch (publishType) {
                case DAILY:
                    addProApprover(approverList, domainQa, approverMap);
                    addProApprover(approverList, domainOwner, approverMap);
                    addProApprover(approverList, finalTestUserList, approverMap);
                    addProApprover(approverList, auditUserList, approverMap);
                    break;
                case EMERGENCY:
                    addProApprover(approverList, domainQa, approverMap);
                    addProApprover(approverList, domainOwner, approverMap);
                    addProApprover(approverList, finalTestUserList, approverMap);
                    addProApprover(approverList, auditUserList, approverMap);
                    //???????????? ????????????????????????
                    List<String> finalUserList = Lists.newArrayList(processConfig.getProEmergencyApprover().split(","));
                    addProApprover(approverList, finalUserList, approverMap);
                    break;
                case FAULT:
                    // ??????????????????????????????
                    addProApprover(approverList, domainQa, approverMap);
                    addProApprover(approverList, finalTestUserList, approverMap);
                    // ???????????????????????????
                    log.warn("?????????????????? apps={}, appOwnerIds={}", JSON.toJSONString(appCodes), JSON.toJSONString(appOwnerUserIds));
                    break;
                case DEVELOP:
                    // ?????????????????????owner????????????????????? cr
                    addProApprover(approverList, crMemberIds, approverMap);
                    addProApprover(approverList, domainOwner, approverMap);
                    addProApprover(approverList, auditUserList, approverMap);
                    domainQa.forEach(qa -> ccBuilder.append(",").append(qa));
                    crMemberIds.forEach(cr -> ccBuilder.append(",").append(cr));
                    break;
                default:
                    break;
            }
        } else {
            switch (publishType) {
                case DAILY:
                case EMERGENCY:
                case FAULT:
                    // ???????????????????????????owner
                    addProApprover(approverList, domainQa, approverMap);
                    break;
                case DEVELOP:
                    // ??????????????????????????????????????????owner???coderiview??????
                    addProApprover(approverList, crMemberIds, approverMap);
                    break;
                default:
                    break;
            }
        }

        req.setApproversV2(approverList);


        ccBuilder.append(",").append(authUser.getUid());
        // ????????????????????????????????????
        ccBuilder.append(",").append(processConfig.getProEmergencyApprover()).append(",").append(processConfig.getProApprover());
        appOwnerUserIds.forEach(ownerId -> ccBuilder.append(",").append(ownerId));
        req.setCcList(ccBuilder.toString());
        // ???????????????
        req.setCcPosition("FINISH");

        // ????????????
        List<FormComponentValueVo> formComponentValueVoList = Lists.newArrayList();
        FormComponentValueVo form1 = new FormComponentValueVo();
        formComponentValueVoList.add(form1);
        form1.setName("??????");
        form1.setValue(grPublishAudit.getPublishName());

        FormComponentValueVo form2 = new FormComponentValueVo();
        formComponentValueVoList.add(form2);
        form2.setName("??????");
        form2.setValue(content.toString());
        req.setFormComponentValues(formComponentValueVoList);

        FormComponentValueVo form3 = new FormComponentValueVo();
        formComponentValueVoList.add(form3);
        form3.setName("????????????");
        String envTypeName;
        if (grPublishAudit.getEnvCode().equalsIgnoreCase("stable")) {
            envTypeName = "????????????";
        } else if(grPublishAudit.getEnvCode().equalsIgnoreCase("pre")) {
            envTypeName = "????????????";
        } else {
            envTypeName = "????????????" + grPublishAudit.getEnvCode();
        }

        form3.setValue(envTypeName);
        req.setFormComponentValues(formComponentValueVoList);

        FormComponentValueVo form4 = new FormComponentValueVo();
        formComponentValueVoList.add(form4);
        form4.setName("????????????");
        form4.setValue(ProcessTypeEnum.RELEASE_SYSTEM.getDesc());

        req.setFormComponentValues(formComponentValueVoList);

        return req;
    }

    private void addProApprover(List<ProcessInstanceApproverVo> approverList, Collection<String> users,
                                Map<String, Boolean> approverMap) {
        if (CollectionUtils.isEmpty(users)) {
            return;
        }

        // ????????????
        List<String> userList = Lists.newArrayList(users);
        List<String> repeatUserList = Lists.newArrayList();
        for (String user : userList) {
            if (approverMap.containsKey(user)) {
                repeatUserList.add(user);
            } else {
                approverMap.put(user, true);
            }
        }
        userList.removeAll(repeatUserList);

        if (CollectionUtils.isNotEmpty(userList)) {
            ProcessInstanceApproverVo approverVo = new ProcessInstanceApproverVo();
            approverVo.setUserIds(userList);
            approverVo.setTaskActionType(userList.size() > 1? "AND": "NONE");
            approverList.add(approverVo);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelPublishAudit(String oid) {
        GrPublishAudit grPublishAudit = grPublishAuditMapper.load(oid);
        if (!PublishAuditEnum.PENDING_APPROVAL.value().equals(grPublishAudit.getAuditStatus())) {
            throw new ServiceException("????????????????????????????????????????????????????????????");
        }

        // 1.?????????????????? -> ???????????????
        grPublishAuditMapper.updateAuditStatus(oid, PublishAuditEnum.CANCELED.value());

        // TODO 2.??????????????????

        // TODO 3.?????????????????????????????????????????????id??????

        // 4.????????????
        GrPublishAuditRecord grPublishAuditRecord = buildPublishAuditRecord(grPublishAudit, grPublishAudit.getDtalkProcessId(), PublishAuditEnum.CANCELED.value(), null);
        grPublishAuditRecordMapper.insert(grPublishAuditRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editPublishAudit(EditPublishAuditRequest request, AuthUser authUser) {
        String oid = request.getOid();
        GrPublishAudit oldGrPublishAudit = grPublishAuditMapper.load(oid);
        // ??????????????????????????????????????????
        List<ChangeOfPublishAuditDTO> oldChangeOfPublishAuditDTOList = grChangeFacade.queryChangeByPublishAuditId(oldGrPublishAudit.getOid());
        List<String> oldLinkedChangeIds = oldChangeOfPublishAuditDTOList.stream()
                .map(ChangeOfPublishAuditDTO::getOid)
                .collect(Collectors.toList());

        if (!(PublishAuditEnum.TO_BE_COMMITTED.value().equals(oldGrPublishAudit.getAuditStatus())
                || PublishAuditEnum.CANCELED.value().equals(oldGrPublishAudit.getAuditStatus()))) {
            throw new ServiceException("?????????????????????????????????????????????????????????");
        }

        // 1.?????????????????????
        GrPublishAudit grPublishAudit = PublishAuditConvertor.INSTANCE.convert(request);
        grPublishAudit.setUpdateUser(currentUserName(authUser));
        grPublishAudit.setUpdateTime(LocalDateTime.now());

        grPublishAuditMapper.update(grPublishAudit);

        // 2.??????????????????????????????
        grChangeFacade.unLinkPublishAudit(grPublishAudit.getOid(), oldLinkedChangeIds, authUser);

        // 3.???????????????
        grChangeFacade.linkPublishAudit(grPublishAudit.getOid(), request.getChangeIds(), authUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createAndSubmitPublishAudit(CreatePublishAuditRequest request, AuthUser authUser) {
        GrPublishAudit publishAudit = createPublishAudit(request, authUser);
        submitPublishAudit(publishAudit.getOid(), authUser);
    }

    @Override
    public void linkChange(String changeId, String publishAuditId, AuthUser authUser) {
        GrPublishAudit oldGrPublishAudit = grPublishAuditMapper.load(publishAuditId);

        if (null == oldGrPublishAudit) {
            throw new ServiceException("??????????????????????????????");
        }

        if (!PublishAuditEnum.TO_BE_COMMITTED.value().equals(oldGrPublishAudit.getAuditStatus())
                || !PublishAuditEnum.CANCELED.value().equals(oldGrPublishAudit.getAuditStatus())) {
            throw new ServiceException("?????????????????????????????????????????????????????????");
        }

        grChangeFacade.linkPublishAudit(publishAuditId, Lists.newArrayList(changeId), authUser);
    }

    private GrPublishAuditRecord buildPublishAuditRecord(GrPublishAudit grPublishAudit, String dingTalkProcessId, String currentStatus, AuthUser authUser) {
        GrPublishAuditRecord grPublishAuditRecord = new GrPublishAuditRecord();

        initSaveModel(grPublishAuditRecord, authUser);
        grPublishAuditRecord.setPublishAuditId(grPublishAudit.getOid());
        grPublishAuditRecord.setPublishAuditName(grPublishAudit.getPublishName());
        grPublishAuditRecord.setDtalkProcessId(dingTalkProcessId);
        grPublishAuditRecord.setEnvType(grPublishAudit.getEnvType());
        grPublishAuditRecord.setEnvCode(grPublishAudit.getEnvCode());
        grPublishAuditRecord.setEnvName(grPublishAudit.getEnvName());
        grPublishAuditRecord.setPublishType(grPublishAudit.getPublishType());
        grPublishAuditRecord.setPreviousStatus(grPublishAudit.getAuditStatus());
        grPublishAuditRecord.setCurrentStatus(currentStatus);

        grPublishAuditRecord.setOid(CommonUtil.getUUIdUseMongo());
        grPublishAuditRecord.setDeleteFlag(GlobalConst.NOT_DELETE);
        grPublishAuditRecord.setCreateTime(LocalDateTime.now());
        grPublishAuditRecord.setUpdateTime(LocalDateTime.now());
        grPublishAuditRecord.setCreateUser(grPublishAudit.getUpdateUser());
        grPublishAuditRecord.setUpdateUser(grPublishAudit.getUpdateUser());

        return grPublishAuditRecord;
    }

    /**
     * ??????????????????????????????
     *
     * @param grPublishAudit ???????????????
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void createPublish(GrPublishAudit grPublishAudit) {
        CreatePublishDTO createPublishDTO = new CreatePublishDTO();

        // ????????????????????????????????????
        createPublishDTO.setPublishName(grPublishAudit.getPublishName());
        createPublishDTO.setPublishAuditId(grPublishAudit.getOid());
        createPublishDTO.setPublishAuditName(grPublishAudit.getPublishName());
        String apolloEnv = null;

        if (EnvTypeEnum.test.value().equals(grPublishAudit.getEnvType())) {
            apolloEnv = "FAT";
        } else if (EnvTypeEnum.pro.value().equals(grPublishAudit.getEnvType())) {
            apolloEnv = "PRO";
        }

        if (null == apolloEnv) {
            throw new ServiceException("??????Apollo????????????");
        }

        createPublishDTO.setApolloEnv(apolloEnv);
        createPublishDTO.setApolloIdc(grPublishAudit.getEnvCode().equalsIgnoreCase(EnvTypeEnum.pre.value())? "pre" : "default");

//        GrChange grChange = new GrChange();
//        grChange.setAuditingId(grPublishAudit.getOid());
        List<ChangeOfPublishAuditDTO> auditDTOList = grChangeFacade.queryChangeListByAuditId(grPublishAudit.getOid());

        for (ChangeOfPublishAuditDTO changeOfPublishAuditDTO : auditDTOList) {

            // ????????????????????????????????????????????????????????????
            grPublishFacade.closePublishByChangeAndEnv(changeOfPublishAuditDTO.getOid(), grPublishAudit.getEnvType(), grPublishAudit.getEnvCode());

            createPublishDTO.setChangeId(changeOfPublishAuditDTO.getOid());
            createPublishDTO.setChangeName(changeOfPublishAuditDTO.getChangeName());
            createPublishDTO.setAppCode(changeOfPublishAuditDTO.getAppCode());
            createPublishDTO.setAppName(changeOfPublishAuditDTO.getAppName());
            createPublishDTO.setDomainArchitectureId(null);
            createPublishDTO.setDomainArchitectureName(null);
            createPublishDTO.setEnvType(grPublishAudit.getEnvType());
            createPublishDTO.setEnvCode(grPublishAudit.getEnvCode());
            createPublishDTO.setEnvName(grPublishAudit.getEnvName());
            createPublishDTO.setPublishType(grPublishAudit.getPublishType());
            createPublishDTO.setPublishStatus(PublishStatusEnum.WAIT_PUBLISH.value());
            createPublishDTO.setDeveloperName(changeOfPublishAuditDTO.getDeveloper());
            createPublishDTO.setQaName(changeOfPublishAuditDTO.getTester());
            // apollo env
            // apollo idc
            createPublishDTO.setGitRepo(changeOfPublishAuditDTO.getGitRepo());
            createPublishDTO.setGitBranch(changeOfPublishAuditDTO.getGitBranch());

            // ?????????id
            createPublishDTO.setOperatorId(grPublishAudit.getInitiatorId());
            createPublishDTO.setCreateUser(grPublishAudit.getCreateUser());
            createPublishDTO.setCreateTime(grPublishAudit.getUpdateTime());
            createPublishDTO.setUpdateUser(grPublishAudit.getCreateUser());
            createPublishDTO.setUpdateTime(grPublishAudit.getUpdateTime());
            createPublishDTO.setDeleteFlag(GlobalConst.NOT_DELETE);
            createPublishDTO.setServiceGroupSwitch("off");
            createPublishDTO.setMqGroupSwitch("off");
            createPublishDTO.setOid(CommonUtil.getUUIdUseMongo());

            GrPublish createRet = grPublishFacade.createPublish(createPublishDTO);
            log.info("GrPublishAuditServiceImpl createPublish ?????????????????????????????? createRet:{}", createRet);
        }
    }

    /**
     * ????????????
     * @param dTalkProcessId
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approvedProcess(String dTalkProcessId) {
        GrPublishAudit audit = this.queryAuditByDTalkProcess(dTalkProcessId);
        if (audit == null) {
            log.error("????????????????????????????????????????????? dTalkProcessId:{}", dTalkProcessId);
            return ;
        }
        // ????????????????????????????????????
        if (!PublishAuditEnum.PENDING_APPROVAL.value().equals(audit.getAuditStatus())) {
            log.error("?????????????????????????????????????????????");
            return;
        }
        // ?????????????????????
        this.updateAuditStatus(audit.getOid(), PublishAuditEnum.APPROVED);

        // ???????????????
        audit.setUpdateTime(LocalDateTime.now());
        audit.setUpdateUser("????????????");



        // ????????????????????????
        this.createPublish(audit);
    }

    /**
     * ???????????????
     * @param dTalkProcessId
     */
    public void refuseProcess(String dTalkProcessId) {
        GrPublishAudit dbAudit = this.queryAuditByDTalkProcess(dTalkProcessId);
        if (dbAudit == null) {
            log.error("??????????????????????????????????????? dTalkProcessId:{}", dTalkProcessId);
            return ;
        }
        // ????????????????????????????????????
        if (!PublishAuditEnum.PENDING_APPROVAL.value().equals(dbAudit.getAuditStatus())) {
            log.error("?????????????????????????????????????????????");
            return;
        }
        // ?????????????????????
        this.updateAuditStatus(dbAudit.getOid(), PublishAuditEnum.REJECTED);
    }

    /**
     * ???????????????
     * @param dTalkProcessId
     */
    public void withdrawProcess(String dTalkProcessId) {
        GrPublishAudit audit = this.queryAuditByDTalkProcess(dTalkProcessId);
        if (audit == null) {
            log.error("??????????????????????????????????????? dTalkProcessId:{}", dTalkProcessId);
            return ;
        }
        // ????????????????????????????????????
        if (!PublishAuditEnum.PENDING_APPROVAL.value().equals(audit.getAuditStatus())) {
            log.error("?????????????????????????????????????????????");
            return;
        }
        // ?????????????????????
        this.updateAuditStatus(audit.getOid(), PublishAuditEnum.CANCELED);
    }

    /**
     * ??????????????????id???????????????
     * @param dTalkProcessId
     * @return
     */
    private GrPublishAudit queryAuditByDTalkProcess(String dTalkProcessId) {
        log.info("GrPublishAuditServiceImpl queryAuditByDTalkProcess ??????????????????id??????????????? dTalkProcessId:{}", dTalkProcessId);
        if (dTalkProcessId == null) {
            return null;
        }
        List<GrPublishAudit> publishAuditList = grPublishAuditMapper.selectByDtalkProcessId(dTalkProcessId);
        if (publishAuditList == null || publishAuditList.size() < 1) {
            log.error("GrPublishAuditServiceImpl queryAuditByDTalkProcess ??????????????????id??????????????? ?????????????????????");
            return null;
        }
        return publishAuditList.get(0);
    }

    /**
     * ?????????????????????????????????
     * @param oid
     * @param auditStatus
     */
    public void updateAuditStatus(String oid, PublishAuditEnum auditStatus) {
        GrPublishAudit updateAudit = new GrPublishAudit();
        updateAudit.setOid(oid);
        updateAudit.setAuditStatus(auditStatus.value());
        updateAudit.setUpdateUser("????????????");
        updateAudit.setUpdateTime(LocalDateTime.now());
        grPublishAuditMapper.update(updateAudit);
    }

    @Override
    public GrPublishAudit getLastAuditByChangeIdAndEnv(String changeId, String envType, String envCode) {
        List<GrPublishAuditChangeRel> rels = grPublishAuditChangeRelMapper.getByChangeId(changeId);
        List<String> auditIds = rels.stream()
                .map(GrPublishAuditChangeRel::getPublishAuditId).distinct().collect(Collectors.toList());
        return grPublishAuditMapper.getLastAuditByEnv(envType, envCode, auditIds);
    }

    /**
     * ?????????????????????id??????????????????
     *
     * @param request
     * @return
     */
    @Override
    public AuditDTalkProcessListResponseDTO getProcessListByDTalkId(AuditDTalkProcessListRequestDTO request) {

        String dtalkProcessId = request.getDtalkProcessId();
        AuditDTalkProcessListResponseDTO responseDTO = new AuditDTalkProcessListResponseDTO();
        responseDTO.setOperationRecordDTOList(Lists.newArrayList());
        responseDTO.setTaskDTOList(Lists.newArrayList());
        if (StringUtils.isBlank(dtalkProcessId)) {
            return responseDTO;
        }

        ProcessInfoQueryDTO queryDTO = new ProcessInfoQueryDTO();
        queryDTO.setInstanceId(dtalkProcessId);
        ResponseData<ProcessInstanceTopVo> processInstance = messageCenterProcessFeignClient.getProcessById(queryDTO);
        if (processInstance.getData() == null && !processInstance.isSuccess()) {
            return responseDTO;
        }
        ProcessInstanceTopVo instanceData = processInstance.getData();
        /**
         * ????????????????????????
         */
        this.setOperationRecords(responseDTO, token, instanceData);

        /**
         * ????????????????????????????????????????????????????????????????????????
         */
        this.setTasks(responseDTO, token, instanceData);

        return responseDTO;
    }

    private void setTasks(AuditDTalkProcessListResponseDTO responseDTO, String bearerToken, ProcessInstanceTopVo instanceData) {
        List<TaskTopVo> tasks = instanceData.getTasks();
        if (CollectionUtils.isNotEmpty(tasks)) {
            List<String> userIdList4Task = tasks.stream().filter(Objects::nonNull)
                .filter(task -> !task.getTaskStatus().equalsIgnoreCase(TasksTaskStatusEnum.CANCELED.getDesc()))
                .map(TaskTopVo::getUserid).collect(Collectors.toList());
            Map<String, String> userIdMap = Maps.newHashMap();
            if (CollectionUtils.isNotEmpty(userIdList4Task)) {
                ResponseData<List<UserDTO>> userDTOListResponseData = userCenterClient.queryUserListByIds(bearerToken, userIdList4Task);
                List<UserDTO> userDTOList = userDTOListResponseData.getData();
                userIdMap = userDTOList.stream().collect(Collectors.toMap(UserDTO::getId, UserDTO::getRealName, (t1, t2) -> t1));
            }
            List<TaskTopVo> sortedTasks = tasks.stream().filter(Objects::nonNull)
                .filter(task -> task.getCreateTime() != null)
                .sorted(Comparator.comparing(TaskTopVo::getCreateTime))
                .collect(Collectors.toList());
            List<TaskTopVo> newTaskStatusList = tasks.stream().filter(Objects::nonNull)
                .filter(task -> task.getTaskStatus().equalsIgnoreCase(TasksTaskStatusEnum.NEW.getCode()))
                .collect(Collectors.toList());
            sortedTasks.addAll(newTaskStatusList);
            for (TaskTopVo task : sortedTasks) {
                if (task.getTaskStatus().equalsIgnoreCase(TasksTaskStatusEnum.CANCELED.getCode())) {
                    continue;
                }
                TaskDTO taskDTO = new TaskDTO();
                taskDTO.setCreateTime(task.getCreateTime() != null ? DateUtil.timeStamp2Date(task.getCreateTime().getTime()) : "");
                taskDTO.setFinishTime(task.getFinishTime() != null ? DateUtil.timeStamp2Date(task.getFinishTime().getTime()) : "");
                taskDTO.setTaskResult(TasksTaskResultEnum.findDescByCode(task.getTaskResult()));
                taskDTO.setTaskStatus(TasksTaskStatusEnum.findDescByCode(task.getTaskStatus()));
                taskDTO.setTaskid(task.getTaskid());
                taskDTO.setUrl(task.getUrl());
                taskDTO.setUserid(task.getUserid());
                taskDTO.setUserName(userIdMap.get(task.getUserid()));

                responseDTO.getTaskDTOList().add(taskDTO);
            }
        }
    }

    private void setOperationRecords(AuditDTalkProcessListResponseDTO responseDTO, String bearerToken, ProcessInstanceTopVo instanceData) {
        List<OperationRecordsVo> operationRecords = instanceData.getOperationRecords();
        if (CollectionUtils.isNotEmpty(operationRecords)) {
            List<String> userIdList4Operation = operationRecords.stream().filter(Objects::nonNull).map(OperationRecordsVo::getUserid).collect(Collectors.toList());
            Map<String, String> userIdMap = Maps.newHashMap();
            if (CollectionUtils.isNotEmpty(userIdList4Operation)) {
                ResponseData<List<UserDTO>> userDTOListResponseData = userCenterClient.queryUserListByIds(bearerToken, userIdList4Operation);
                List<UserDTO> userDTOList = userDTOListResponseData.getData();
                userIdMap = userDTOList.stream().collect(Collectors.toMap(UserDTO::getId, UserDTO::getRealName, (t1, t2) -> t1));
            }
            for (OperationRecordsVo operationRecord : operationRecords) {
                OperationRecordDTO operationRecordDTO = new OperationRecordDTO();
                operationRecordDTO.setDate(operationRecord.getDate() != null ? DateUtil.timeStamp2Date(operationRecord.getDate().getTime()) : "");
                operationRecordDTO.setOperationResult(OperationRecordsOperationResultEnum.findDescByCode(operationRecord.getOperationResult()));
                operationRecordDTO.setOperationType(OperationRecordsOperationTypeEnum.findDescByCode(operationRecord.getOperationType()));
                operationRecordDTO.setRemark(operationRecord.getRemark());
                operationRecordDTO.setUserid(operationRecord.getUserid());
                operationRecordDTO.setUserName(userIdMap.get(operationRecord.getUserid()));
                operationRecordDTO.setAttachments(operationRecord.getAttachments());

                responseDTO.getOperationRecordDTOList().add(operationRecordDTO);
            }
        }
    }
}
