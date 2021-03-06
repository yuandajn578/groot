package com.choice.cloud.architect.groot.service.inner.impl;

import com.choice.cloud.architect.groot.convertor.EnvConvertor;
import com.choice.cloud.architect.groot.dao.GrAppEnvDefaultConfigMapper;
import com.choice.cloud.architect.groot.dao.GrEnvMapper;
import com.choice.cloud.architect.groot.dataobject.EnvQueryPageDO;
import com.choice.cloud.architect.groot.dto.*;
import com.choice.cloud.architect.groot.enums.EnvTypeEnum;
import com.choice.cloud.architect.groot.exception.ServiceException;
import com.choice.cloud.architect.groot.model.GrAppEnvDefaultConfig;
import com.choice.cloud.architect.groot.model.GrAppEnvRel;
import com.choice.cloud.architect.groot.model.GrEnv;
import com.choice.cloud.architect.groot.option.GlobalConst;
import com.choice.cloud.architect.groot.option.WebPage;
import com.choice.cloud.architect.groot.remote.grayconfigcenter.GrayConfigCenterClient;
import com.choice.cloud.architect.groot.remote.grayconfigcenter.GrayRuleGroup;
import com.choice.cloud.architect.groot.remote.milkyway.AppConfigInfoResponseDTO;
import com.choice.cloud.architect.groot.remote.milkyway.MilkywayService;
import com.choice.cloud.architect.groot.request.*;
import com.choice.cloud.architect.groot.response.ResponseData;
import com.choice.cloud.architect.groot.response.code.EnvResponseCode;
import com.choice.cloud.architect.groot.service.inner.*;
import com.choice.cloud.architect.groot.util.CommonUtil;
import com.choice.driver.jwt.entity.AuthUser;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.choice.cloud.architect.groot.enums.DeployStrategyEnum.START_FIRST;

/**
 * @author zhangkun
 */
@Service
@Slf4j
public class GrEnvServiceImpl extends BaseService implements GrEnvService {
    @Autowired
    private GrEnvMapper grEnvMapper;
    @Resource(name = "offlineKubernetesClient")
    private KubernetesClient offlineKubernetesClient;
    @Resource(name = "onlineKubernetesClient")
    private KubernetesClient onlineKubernetesClient;
    @Autowired
    private GrayConfigCenterClient grayConfigCenterClient;
    @Autowired
    private GrAppEnvRelService grAppEnvRelService;
    @Autowired
    private GrAppEnvDefaultConfigMapper grAppEnvDefaultConfigMapper;
    @Autowired
    private GrChangeService grChangeService;
    @Autowired
    private GrFormChangeEnvService grFormChangeEnvService;
    @Autowired
    private ContainerManageService containerManageService;
    @Autowired
    private MilkywayService milkywayService;

    public static final List<String> NOT_ALLOWED_DEL_ENV = Lists.newArrayList("test-stable", "pro-stable", "pro-pre");

    /**
     * ??????????????????
     * @param envCreateRequest
     * @param authUser
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createEnv(CreateEnvRequest envCreateRequest, AuthUser authUser) {

        // ?????????????????????????????????????????????
        this.checkSensitiveEnv(envCreateRequest);

        // ??????????????????????????????????????????????????????
        boolean envCodeExisted = this.checkIfEnvCodeExisted(envCreateRequest.getEnvType(), envCreateRequest.getEnvCode());
        if (envCodeExisted) {
            // ??????????????????????????????
            AddChangeBindEnvRequest addChangeBindEnvRequest = new AddChangeBindEnvRequest();
            addChangeBindEnvRequest.setAppCode(envCreateRequest.getAppCode());
            addChangeBindEnvRequest.setChangeId(envCreateRequest.getChangeId());
            addChangeBindEnvRequest.setEnvCode(envCreateRequest.getEnvCode());
            addChangeBindEnvRequest.setEnvType(envCreateRequest.getEnvType());
            // ?????????????????????????????????
            this.addChangeBindEnv(addChangeBindEnvRequest, authUser);
            return;
        }

        // ??????????????????
        GrEnv grEnv = new GrEnv();
        BeanUtils.copyProperties(envCreateRequest, grEnv);
        initSaveModel(grEnv, authUser);
        grEnv.setStatus(GlobalConst.ENABLE);
        int insertRows = grEnvMapper.insert(grEnv);

        if (insertRows != 1) {
            return;
        }
        // ????????????????????????k8s???????????????
        this.createKubernetesNamespace(envCreateRequest);
        // ????????????-??????-??????????????????????????????????????????????????????????????????????????????????????????????????????
        this.saveAppEnvRealInfoAndDefaultConfig(envCreateRequest, authUser, grEnv);
    }

    /**
     * ????????????-??????-???????????????????????????????????????????????????
     * @param envCreateRequest
     * @param authUser
     * @param grEnv
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveAppEnvRealInfoAndDefaultConfig(CreateEnvRequest envCreateRequest, AuthUser authUser, GrEnv grEnv) {
        // ????????????-??????-??????????????????
        grAppEnvRelService.saveAppAndEnvRel(envCreateRequest.getChangeId(), envCreateRequest.getAppCode(), envCreateRequest.getEnvType(), envCreateRequest.getEnvCode(), authUser);

        // ??????????????????
        if (null != envCreateRequest.getPublishConfig()) {
            GrAppEnvDefaultConfig grAppEnvDefaultConfig = new GrAppEnvDefaultConfig();
            BeanUtils.copyProperties(envCreateRequest.getPublishConfig(), grAppEnvDefaultConfig);
            initSaveModel(grAppEnvDefaultConfig, authUser);
            grAppEnvDefaultConfig.setAppCode(envCreateRequest.getAppCode());
            grAppEnvDefaultConfig.setEnvCode(grEnv.getEnvCode());
            grAppEnvDefaultConfig.setEnvType(grEnv.getEnvType());

            grAppEnvDefaultConfigMapper.insert(grAppEnvDefaultConfig);
        }
    }

    /**
     * ????????????????????????k8s???????????????
     * @param envCreateRequest
     */
    private void createKubernetesNamespace(CreateEnvRequest envCreateRequest) {
        String nsName = envCreateRequest.getEnvType().concat("-").concat(envCreateRequest.getEnvCode());
        Namespace ns = new NamespaceBuilder().withNewMetadata().withName(nsName).endMetadata().build();
        Namespace createdNamespace = null;

        // TODO: 2020/6/10 ?????????containerManageService
        if (nsName.startsWith(EnvTypeEnum.test.value())) {
            Namespace offlineNamespace = createOfflineNamespace(nsName);
            createdNamespace= offlineKubernetesClient.namespaces().createOrReplace(offlineNamespace);
            log.info("created test Namespace {} created", createdNamespace.getMetadata().getName());
        } else if (nsName.startsWith(EnvTypeEnum.pro.value())) {
            createdNamespace= onlineKubernetesClient.namespaces().createOrReplace(ns);
            log.info("created pro Namespace {} created", createdNamespace.getMetadata().getName());
        }
    }

    /**
     * ?????????????????????????????????????????????
     * ??????????????????????????????????????? {@link EnvTypeEnum}
     * @param envCreateRequest
     */
    private void checkSensitiveEnv(CreateEnvRequest envCreateRequest) {
        Set<String> sensitiveEnvNames = Sets.newHashSet();
        for (EnvTypeEnum envTypeEnum : EnvTypeEnum.values()) {
            sensitiveEnvNames.add(envTypeEnum.value());
            sensitiveEnvNames.add(envTypeEnum.description());
        }

        if (sensitiveEnvNames.contains(envCreateRequest.getEnvName())
            || sensitiveEnvNames.contains(envCreateRequest.getEnvCode())) {
            throw new ServiceException(EnvResponseCode.SERVICE_GROUP_NAME_SENSITIVE);
        }
    }

    @Override
    public WebPage<List<ListEnvDTO>> list(ListEnvRequest listEnvRequest) {
        Integer pageNum = listEnvRequest.getPageNum();
        Integer pageSize = listEnvRequest.getPageSize();
        List<ListEnvDTO> envDTOList = Lists.newArrayList();
        PageHelper.startPage(pageNum, pageSize);

        EnvQueryPageDO envQueryPageDO = EnvConvertor.INSTANCE.convert(listEnvRequest);
        List<GrEnv> grEnvList = grEnvMapper.listWithPage(envQueryPageDO);

        if (CollectionUtils.isEmpty(grEnvList)) {
            return new WebPage<>(pageNum, pageSize, 0, null);
        }

        for (GrEnv grEnv : grEnvList) {
            ListEnvDTO envDTO = new ListEnvDTO();
            BeanUtils.copyProperties(grEnv, envDTO);
            envDTOList.add(envDTO);
        }

        PageInfo<GrEnv> info = new PageInfo<>(grEnvList);
        return new WebPage<>(pageNum, pageSize, info.getTotal(), envDTOList);
    }

    @Override
    public boolean existsEnv(String id) {
        GrEnv grEnv = grEnvMapper.selectById(id);

        if (null != grEnv) {
            return true;
        }

        return false;
    }

    /**
     * ??????????????????????????????????????????????????????
     * @param envType
     * @param envCode
     * @return true ??????  false ?????????
     */
    @Override
    public boolean checkIfEnvCodeExisted(String envType, String envCode) {
        GrEnv grEnv = grEnvMapper.selectByCode(envType, envCode);

        if (null != grEnv) {
            return true;
        }

        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delEnv(String id, AuthUser authUser) {
        GrEnv grEnv = grEnvMapper.selectById(id);
        ResponseData<List<GrayRuleGroup>> grayRuleWithEnv = grayConfigCenterClient.getGrayRuleWithEnv(CommonUtil.currentRequestToken(), grEnv.getEnvType(), grEnv.getEnvCode());

        if (CollectionUtils.isNotEmpty(grayRuleWithEnv.getData())) {
           throw new ServiceException(EnvResponseCode.EXISTED_GRAY_CONFIG_NOT_DELETE);
        }

        String nsName = grEnv.getEnvType().concat("-").concat(grEnv.getEnvCode());

        if (NOT_ALLOWED_DEL_ENV.contains(nsName)) {
            throw new ServiceException(EnvResponseCode.NOT_ALLOW_DEL_ENV);
        }

        // TODO: 2020/6/10 ?????????containerManageService
        if (null == offlineKubernetesClient.namespaces().withName(nsName).get()) {
            log.info("k8s namespace ??????????????????????????? [{}] ????????????", nsName);
        } else {
            if (nsName.startsWith("test")) {
                Boolean delete = offlineKubernetesClient.namespaces().withName(nsName).delete();
                if (delete) {
                    log.info("k8s namespace [{}] ????????????", nsName);
                } else {
                    throw new ServiceException(EnvResponseCode.K8S_NAMESPACE_DELETE_FAILURE);
                }
            } else if (nsName.startsWith("pro")) {
                log.info("k8s namespace [{}] ????????????", nsName);
                Boolean delete = onlineKubernetesClient.namespaces().withName(nsName).delete();
                if (delete) {
                } else {
                    throw new ServiceException(EnvResponseCode.K8S_NAMESPACE_DELETE_FAILURE);
                }
            }
        }

        grEnvMapper.delete(id, currentUserName(authUser));

        RemoveChangeBindEnvRequest remove = new RemoveChangeBindEnvRequest();
        remove.setEnvCode(grEnv.getEnvCode());
        remove.setEnvType(grEnv.getEnvType());
        grAppEnvRelService.removeChangeBindEnv(remove);
        // ??????????????????????????????

        grFormChangeEnvService.deleteEnv(id);
        // ??????????????????????????????
        log.info("GrEnvServiceImpl delEnv ???????????? ENVCode:{},authUser:{}", id, authUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(ChangeEnvStatusRequest changeEnvStatusRequest, AuthUser authUser) {
        List<String> envIdList = Splitter.on(",").splitToList(changeEnvStatusRequest.getIds());

        for (String envId : envIdList) {
            if (changeEnvStatusRequest.getExpectStatus().equals(GlobalConst.DISABLE)) {
                GrEnv grEnv = grEnvMapper.selectById(envId);
                ResponseData<List<GrayRuleGroup>> grayRuleWithEnv = grayConfigCenterClient.getGrayRuleWithEnv(CommonUtil.currentRequestToken(), grEnv.getEnvType(), grEnv.getEnvCode());

                if (CollectionUtils.isNotEmpty(grayRuleWithEnv.getData())) {
                    throw new ServiceException(EnvResponseCode.EXISTED_GRAY_CONFIG_NOT_DELETE);
                }
            }

            grEnvMapper.updateStatus(envId, changeEnvStatusRequest.getOldStatus(), changeEnvStatusRequest.getExpectStatus(), currentUserName(authUser));
        }
    }

    @Override
    public List<SelectListEnvDTO> selectList(ListEnvRequest request) {
        EnvQueryPageDO envQueryPageDO = EnvConvertor.INSTANCE.convert(request);
        List<GrEnv> grEnvList = grEnvMapper.listWithPage(envQueryPageDO);
        List<SelectListEnvDTO> selectListEnvDTOList = Lists.newArrayList();

        for (GrEnv grEnv : grEnvList) {
            SelectListEnvDTO selectListEnvDTO = EnvConvertor.INSTANCE.convert(grEnv);

            selectListEnvDTOList.add(selectListEnvDTO);
        }


        return selectListEnvDTOList;
    }

    @Override
    public Map<String, List<ListAppEnvDTO>> listEnvByApp(ListEnvByAppRequest request) {
        Map<String, List<ListAppEnvDTO>> appEnvDetailMap = Maps.newHashMap();
        appEnvDetailMap.put(EnvTypeEnum.test.value(), Lists.newArrayList());


        List<String> appCodeList = Lists.newArrayList(request.getAppCode());
        List<GrAppEnvRel> grAppEnvRels = grAppEnvRelService.listByAppCode(appCodeList);

        if (CollectionUtils.isEmpty(grAppEnvRels)) {
            return appEnvDetailMap;
        }

        Map<String, List<GrAppEnvRel>> envType2EnvRelMap = grAppEnvRels.stream().collect(Collectors.groupingBy(GrAppEnvRel::getEnvType));
        if (envType2EnvRelMap.isEmpty()) {
            return appEnvDetailMap;
        }

        appEnvDetailBuild(appEnvDetailMap, envType2EnvRelMap, EnvTypeEnum.test.value());
        // 2020.5.29 ?????????????????????????????????????????????????????????
        List<GrEnv> proEnvs = grEnvMapper.selectByType(EnvTypeEnum.pro.value());
        List<ListAppEnvDTO> listAppEnvDTO = proEnvs.stream().map(env -> {
            ListAppEnvDTO appEnvDTO = new ListAppEnvDTO();
            BeanUtils.copyProperties(env, appEnvDTO);
            return appEnvDTO;
        }).collect(Collectors.toList());
        appEnvDetailMap.put(EnvTypeEnum.pro.value(), listAppEnvDTO);
        return appEnvDetailMap;
    }

    /**
     * ????????????????????????????????????????????????????????????
     * @param oid
     * @param appCode
     * @return
     */
    @Override
    public AppEnvDetailDTO getDetailByAppAndEnv(String oid, String appCode) {
        AppEnvDetailDTO appEnvDetailDTO = new AppEnvDetailDTO();

        GrEnv grEnv = grEnvMapper.selectById(oid);

        if (null == grEnv) {
            return appEnvDetailDTO;
        }

        // TODO: 2020/4/26 ??????appCode???????????????????????????????????????

        BeanUtils.copyProperties(grEnv, appEnvDetailDTO);

        // ?????????????????????????????????
        this.setPublicConfigByPriority(appCode, appEnvDetailDTO, grEnv);

        return appEnvDetailDTO;
    }

    /**
     * ?????????????????????????????????
     * @param appCode
     * @param appEnvDetailDTO
     * @param grEnv
     */
    private void setPublicConfigByPriority(String appCode, AppEnvDetailDTO appEnvDetailDTO, GrEnv grEnv) {
        // ???????????????????????????????????????????????????
        GrAppEnvDefaultConfig grAppEnvDefaultConfig = grAppEnvDefaultConfigMapper.getByAppAndEnv(appCode, grEnv.getEnvType(), grEnv.getEnvCode(), GlobalConst.NOT_DELETE);

        if (null != grAppEnvDefaultConfig) {
            AppEnvPublishConfigDTO appEnvPublishConfigDTO = new AppEnvPublishConfigDTO();
            BeanUtils.copyProperties(grAppEnvDefaultConfig, appEnvPublishConfigDTO);
            appEnvDetailDTO.setPublishConfig(appEnvPublishConfigDTO);
        } else {
            // ????????????????????????????????????????????????
            AppEnvPublishConfigDTO appEnvPublishConfigDTO = new AppEnvPublishConfigDTO();
            // ??????????????????
            AppConfigInfoResponseDTO appConfigInfo = milkywayService.getAppConfigInfoByAppCode(appCode);
            if (null != appConfigInfo) {
                BeanUtils.copyProperties(appConfigInfo, appEnvPublishConfigDTO);
                // ????????????????????????????????????????????????????????????
                appEnvPublishConfigDTO.setHealthCheckPeriodSeconds(Integer.parseInt(appConfigInfo.getHealthCheckPeriodSeconds()));
                appEnvPublishConfigDTO.setHealthCheckInitialDelaySeconds(Integer.parseInt(appConfigInfo.getHealthCheckInitialDelaySeconds()));
                appEnvPublishConfigDTO.setHealthCheckTimeoutSeconds(Integer.parseInt(appConfigInfo.getHealthCheckTimeoutSeconds()));
                appEnvPublishConfigDTO.setHealthCheckSuccessThreshold(Integer.parseInt(appConfigInfo.getHealthCheckSuccessThreshold()));
                appEnvPublishConfigDTO.setHealthCheckFailureThreshold(Integer.parseInt(appConfigInfo.getHealthCheckFailureThreshold()));
                appEnvPublishConfigDTO.setBatchSize(Integer.parseInt(appConfigInfo.getBatchSize()));
                // ????????????:startFirst???stopFirst???deleteAllFirst
                // ????????????startFirst
                appEnvPublishConfigDTO.setDeployStrategy(START_FIRST.getValue());
                // ?????????mq??????off
                appEnvPublishConfigDTO.setMqGraySwitch("off");
                appEnvPublishConfigDTO.setMqGrayProducerSwitch("off");
                appEnvPublishConfigDTO.setMqGrayConsumerSwitch("off");
            }
            appEnvDetailDTO.setPublishConfig(appEnvPublishConfigDTO);
        }
    }

    /**
     * ???????????????????????????????????????
     * ??????????????????????????????????????????
     * @param request
     * @param authUser
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editEnv(EditEnvRequest request, AuthUser authUser) {
        GrEnv grEnv = grEnvMapper.selectById(request.getOid());

        if (null == grEnv) {
            throw new ServiceException("??????????????????");
        }

        BeanUtils.copyProperties(request, grEnv);

        // ????????????????????????
        grEnv.setUpdateUser(getUserName(authUser));
        grEnv.setUpdateTime(LocalDateTime.now());
        grEnvMapper.update(grEnv);

        // ????????????-??????-????????????
        GrAppEnvDefaultConfig appEnvDefaultConfig = grAppEnvDefaultConfigMapper.getByAppAndEnv(request.getAppCode(), grEnv.getEnvType(), grEnv.getEnvCode(), GlobalConst.NOT_DELETE);

        if (null != appEnvDefaultConfig) {
            BeanUtils.copyProperties(request.getPublishConfig(), appEnvDefaultConfig);
            appEnvDefaultConfig.setUpdateUser(getUserName(authUser));
            appEnvDefaultConfig.setUpdateTime(LocalDateTime.now());

            grAppEnvDefaultConfigMapper.update(appEnvDefaultConfig);
        } else {
            GrAppEnvDefaultConfig grAppEnvDefaultConfig = new GrAppEnvDefaultConfig();
            BeanUtils.copyProperties(request.getPublishConfig(), grAppEnvDefaultConfig);
            initSaveModel(grAppEnvDefaultConfig, authUser);
            grAppEnvDefaultConfig.setAppCode(request.getAppCode());
            grAppEnvDefaultConfig.setEnvCode(grEnv.getEnvCode());
            grAppEnvDefaultConfig.setEnvType(grEnv.getEnvType());

            grAppEnvDefaultConfigMapper.insert(grAppEnvDefaultConfig);
        }
    }

    /**
     * ?????????????????????????????????/????????????
     * @param request
     * @param authUser
     * @param grEnv
     */
    @Transactional(rollbackFor = Exception.class)
    public void getRealAndSaveConfig(EditEnvRequest request, AuthUser authUser, GrEnv grEnv) {
        // ????????????-??????-????????????
        GrAppEnvDefaultConfig appEnvDefaultConfig = grAppEnvDefaultConfigMapper.getByAppAndEnv(request.getAppCode(), grEnv.getEnvType(), grEnv.getEnvCode(), GlobalConst.NOT_DELETE);

        // ???????????????
        if (null != appEnvDefaultConfig) {
            BeanUtils.copyProperties(request.getPublishConfig(), appEnvDefaultConfig);
            appEnvDefaultConfig.setUpdateUser(getUserName(authUser));
            appEnvDefaultConfig.setUpdateTime(LocalDateTime.now());

            grAppEnvDefaultConfigMapper.update(appEnvDefaultConfig);
        } else {
            // ????????????????????????????????????????????????
            GrAppEnvDefaultConfig grAppEnvDefaultConfig = new GrAppEnvDefaultConfig();
            // ??????????????????
            AppConfigInfoResponseDTO appConfigInfo = milkywayService.getAppConfigInfoByAppCode(request.getAppCode());
            if (null != appConfigInfo) {
                BeanUtils.copyProperties(appConfigInfo, grAppEnvDefaultConfig);
                // ????????????????????????????????????????????????????????????
                grAppEnvDefaultConfig.setHealthCheckPeriodSeconds(Integer.parseInt(appConfigInfo.getHealthCheckPeriodSeconds()));
                grAppEnvDefaultConfig.setHealthCheckInitialDelaySeconds(Integer.parseInt(appConfigInfo.getHealthCheckInitialDelaySeconds()));
                grAppEnvDefaultConfig.setHealthCheckTimeoutSeconds(Integer.parseInt(appConfigInfo.getHealthCheckTimeoutSeconds()));
                grAppEnvDefaultConfig.setHealthCheckSuccessThreshold(Integer.parseInt(appConfigInfo.getHealthCheckSuccessThreshold()));
                grAppEnvDefaultConfig.setHealthCheckFailureThreshold(Integer.parseInt(appConfigInfo.getHealthCheckFailureThreshold()));
                grAppEnvDefaultConfig.setBatchSize(Integer.parseInt(appConfigInfo.getBatchSize()));
                // ????????????:startFirst???stopFirst???deleteAllFirst
                // ????????????startFirst
                grAppEnvDefaultConfig.setDeployStrategy(START_FIRST.getValue());
                // ?????????mq??????off
                grAppEnvDefaultConfig.setMqGraySwitch("off");
                grAppEnvDefaultConfig.setMqGrayProducerSwitch("off");
                grAppEnvDefaultConfig.setMqGrayConsumerSwitch("off");
            } else {
                BeanUtils.copyProperties(request.getPublishConfig(), grAppEnvDefaultConfig);
            }
            initSaveModel(grAppEnvDefaultConfig, authUser);
            grAppEnvDefaultConfig.setAppCode(request.getAppCode());
            grAppEnvDefaultConfig.setEnvCode(grEnv.getEnvCode());
            grAppEnvDefaultConfig.setEnvType(grEnv.getEnvType());
            grAppEnvDefaultConfigMapper.insert(grAppEnvDefaultConfig);
        }
    }

    @Override
    public List<String> getEnvBindApp(QueryEnvBindAppRequest request) {
        List<GrAppEnvRel> appEnvRels = grAppEnvRelService.listByEnv(request.getEnvType(), request.getEnvCode());

        if (CollectionUtils.isEmpty(appEnvRels)) {
            log.info("envType = {}, envCode = {}?????????????????????", request.getEnvType(), request.getEnvCode());
            return Collections.emptyList();
        }

        List<String> appCodeList = appEnvRels.stream().map(GrAppEnvRel::getAppCode).collect(Collectors.toList());

        return appCodeList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindChangeEnv(String changeId, String appCode, String envId, AuthUser authUser) {
        GrEnv grEnv = grEnvMapper.selectById(envId);

        grAppEnvRelService.saveAppAndEnvRel(changeId, appCode, grEnv.getEnvType(), grEnv.getEnvCode(), authUser);

        // ????????????-??????-??????????????????
        GrAppEnvDefaultConfig grAppEnvDefaultConfig = new GrAppEnvDefaultConfig();
        initSaveModel(grAppEnvDefaultConfig, authUser);
        grAppEnvDefaultConfig.setAppCode(appCode);
        // TODO envType/envCode
        grAppEnvDefaultConfigMapper.insert(grAppEnvDefaultConfig);
    }

    @Override
    public GrAppEnvDefaultConfigDTO getAppPublishConfig(QueryAppPublishConfigRequest request) {
        GrAppEnvDefaultConfig appEnvDefaultConfig = grAppEnvDefaultConfigMapper.getByAppAndEnv(request.getAppCode(), request.getEnvType(), request.getEnvCode(), GlobalConst.NOT_DELETE);
        GrAppEnvDefaultConfigDTO appEnvDefaultConfigDTO = new GrAppEnvDefaultConfigDTO();

        if (null == appEnvDefaultConfig) {
            BeanUtils.copyProperties(request, appEnvDefaultConfigDTO);
            return appEnvDefaultConfigDTO;
        }

        BeanUtils.copyProperties(appEnvDefaultConfig, appEnvDefaultConfigDTO);

        return appEnvDefaultConfigDTO;
    }

    /**
     * ?????????????????????????????????????????????
     * @param changeId
     * @return
     */
    @Override
    public List<ChangeBindEnvDTO> getChangeBindEnv(String changeId) {
        List<ChangeBindEnvDTO> changeBindEnvList = grAppEnvRelService.getChangeBindEnv(changeId);

        if (CollectionUtils.isNotEmpty(changeBindEnvList)) {
            for (ChangeBindEnvDTO changeBindEnvDTO : changeBindEnvList) {
                GrEnv grEnv = grEnvMapper.selectByCode(changeBindEnvDTO.getEnvType(), changeBindEnvDTO.getEnvCode());
                changeBindEnvDTO.setEnvName(grEnv.getEnvName());
            }
        }

        return changeBindEnvList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addChangeBindEnv(AddChangeBindEnvRequest request, AuthUser authUser) {
        grAppEnvRelService.addChangeBindEnv(request, authUser);
    }

    @Override
    public WebPage<List<ListChangeDTO>> getEnvBindChangeList(QueryEnvBindChangeRequest request, AuthUser authUser) {
        Integer pageNum = request.getPageNum();
        Integer pageSize = request.getPageSize();
        List<GrAppEnvRel> appEnvRels = grAppEnvRelService.listByEnv(request.getEnvType(), request.getEnvCode());

        if (CollectionUtils.isEmpty(appEnvRels)) {
            return new WebPage<>(pageNum, pageSize, 0, null);
        }

        Set<String> changeIds = Sets.newHashSet();
        for (GrAppEnvRel appEnvRel : appEnvRels) {
            if ("-1".equals(appEnvRel.getChangeId())) {
                continue;
            }

            changeIds.add(appEnvRel.getChangeId());
        }

        if (CollectionUtils.isEmpty(changeIds)) {
            return new WebPage<>(pageNum, pageSize, 0, null);
        }

        ListChangeRequest listChangeRequest = new ListChangeRequest();
        listChangeRequest.setPageNum(pageNum);
        listChangeRequest.setPageSize(pageSize);
        listChangeRequest.setIds(Lists.newArrayList(changeIds.iterator()));

        return grChangeService.list(listChangeRequest);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeChangeBindEnv(RemoveChangeBindEnvRequest request) {
        grAppEnvRelService.removeChangeBindEnv(request);
        String namespaceName = request.getEnvType().concat("-").concat(request.getEnvCode());
        ListChangeDTO changeDTO = grChangeService.queryByOid(request.getChangeId());
        if (changeDTO == null) {
            throw new ServiceException("??????????????????");
        }
        containerManageService.removeDeployment(namespaceName, changeDTO.getAppCode());
    }

    @Override
    public Map<String, List<SelectListEnvDTO>> selectListEnvGroup(ListEnvRequest request) {
        Map<String, List<SelectListEnvDTO>> envType4DetailMap = Maps.newHashMap();

        List<SelectListEnvDTO> envInTest = getEnvDetailWithType(EnvTypeEnum.test.value(), request.getEnvName());
        List<SelectListEnvDTO> envInPro = getEnvDetailWithType(EnvTypeEnum.pro.value(), request.getEnvName());

        envType4DetailMap.put("test", envInTest);
        envType4DetailMap.put("pro", envInPro);

        return envType4DetailMap;
    }

    private List<SelectListEnvDTO> getEnvDetailWithType(String envType, String envName) {
        List<SelectListEnvDTO> envDetailList = Lists.newArrayList();

        EnvQueryPageDO envQueryPageDO4Test = new EnvQueryPageDO();
        envQueryPageDO4Test.setEnvType(envType);
        envQueryPageDO4Test.setEnvName(envName);

        List<GrEnv> grEnvList = grEnvMapper.listWithPage(envQueryPageDO4Test);
        if (CollectionUtils.isNotEmpty(grEnvList)) {
            for (GrEnv grEnv : grEnvList) {
                SelectListEnvDTO selectListEnvDTO = new SelectListEnvDTO();
                BeanUtils.copyProperties(grEnv, selectListEnvDTO);
                envDetailList.add(selectListEnvDTO);
            }
        }

        return envDetailList;
    }

    private void appEnvDetailBuild(Map<String, List<ListAppEnvDTO>> appEnvDetailMap, Map<String, List<GrAppEnvRel>> envType2EnvRelMap, String envType) {
        List<GrAppEnvRel> appEnvRelInDev = envType2EnvRelMap.get(envType);
        List<ListAppEnvDTO> appEnvInDev = Lists.newArrayList();

        if (CollectionUtils.isNotEmpty(appEnvRelInDev)) {
            for (GrAppEnvRel grAppEnvRel : appEnvRelInDev) {
                GrEnv grEnv = grEnvMapper.selectByCode(envType, grAppEnvRel.getEnvCode());
                ListAppEnvDTO listAppEnvDTO = new ListAppEnvDTO();

                BeanUtils.copyProperties(grEnv, listAppEnvDTO);
                appEnvInDev.add(listAppEnvDTO);
            }

            appEnvDetailMap.put(envType, appEnvInDev);
        } else {
            appEnvDetailMap.put(envType, appEnvInDev);
        }
    }

    private Namespace createOfflineNamespace(String nsName) {
        Namespace namespace = new Namespace();
        Map<String, String> annotations = new HashMap<>();
        annotations.put("field.cattle.io/containerDefaultResourceLimit", "{\"limitsCpu\":\"2000m\",\"limitsMemory\":\"4096Mi\",\"requestsCpu\":\"100m\",\"requestsMemory\":\"2048Mi\"}");

        ObjectMeta objectMeta = new ObjectMeta();
        objectMeta.setAnnotations(annotations);
        objectMeta.setName(nsName);
        namespace.setMetadata(objectMeta);

        return namespace;
    }
}
