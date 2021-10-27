package com.choice.cloud.architect.groot.service.inner.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import com.alibaba.fastjson.JSON;

import com.choice.cloud.architect.groot.constant.ApolloChangeLogConstant;
import com.choice.cloud.architect.groot.constant.ApolloConstant;
import com.choice.cloud.architect.groot.convertor.ApolloChangeConvertor;
import com.choice.cloud.architect.groot.dao.GrApolloChangeOrderMapper;
import com.choice.cloud.architect.groot.dao.GrChangeRecodeMapper;
import com.choice.cloud.architect.groot.dto.ListApolloChangeDTO;
import com.choice.cloud.architect.groot.dto.ListApolloUpdateItemRequestDTO;
import com.choice.cloud.architect.groot.dto.ListApolloUpdateItemResponseDTO;
import com.choice.cloud.architect.groot.dto.PageListApolloCommitHistoryRequestDTO;
import com.choice.cloud.architect.groot.dto.PageListApolloCommitHistoryResponseDTO;
import com.choice.cloud.architect.groot.dto.apollo.ApolloPageQueryCommitItemHistoryListResponseDTO;
import com.choice.cloud.architect.groot.dto.apollo.ApolloPageQueryCommitItemHistoryListResponseDTO.CreateItemsBean;
import com.choice.cloud.architect.groot.dto.apollo.ApolloPageQueryCommitItemHistoryListResponseDTO.DeleteItemsBean;
import com.choice.cloud.architect.groot.dto.apollo.ApolloPageQueryCommitItemHistoryListResponseDTO.UpdateItemsBean;
import com.choice.cloud.architect.groot.dto.apollo.ApolloPageQueryCommitItemHistoryListResponseDTO.UpdateItemsBean.NewItemBean;
import com.choice.cloud.architect.groot.dto.apollo.ApolloPageQueryCommitItemHistoryListResponseDTO.UpdateItemsBean.OldItemBean;
import com.choice.cloud.architect.groot.dto.apollo.ApolloQueryAllItemByEnvIdcNamespaceResponseDTO;
import com.choice.cloud.architect.groot.dto.apollo.ApolloQueryItemDTO;
import com.choice.cloud.architect.groot.enums.ApolloCommitHistoryTypeEnum;
import com.choice.cloud.architect.groot.enums.ApolloEnvAndIdcEnum;
import com.choice.cloud.architect.groot.enums.ApolloItemStatusEnum;
import com.choice.cloud.architect.groot.enums.ApolloNamespaceEnum;
import com.choice.cloud.architect.groot.enums.ApolloPublishTypeEnum;
import com.choice.cloud.architect.groot.enums.ChangeLogTypeEnum;
import com.choice.cloud.architect.groot.enums.ChangeOptEnum;
import com.choice.cloud.architect.groot.enums.OperateResultEnum;
import com.choice.cloud.architect.groot.exception.ServiceException;
import com.choice.cloud.architect.groot.model.GrApolloChangeOrder;
import com.choice.cloud.architect.groot.model.GrChangeRecode;
import com.choice.cloud.architect.groot.option.GlobalConst;
import com.choice.cloud.architect.groot.request.CreateApolloChangeRequest;
import com.choice.cloud.architect.groot.request.DeleteApolloChangeRequest;
import com.choice.cloud.architect.groot.request.ListApolloChangeRequest;
import com.choice.cloud.architect.groot.request.PublishApolloChangeRequest;
import com.choice.cloud.architect.groot.request.UpdateApolloChangeRequest;
import com.choice.cloud.architect.groot.response.code.ApolloResponseCode;
import com.choice.cloud.architect.groot.service.inner.ApolloClientService;
import com.choice.cloud.architect.groot.service.inner.GrApolloChangeService;
import com.choice.cloud.architect.groot.service.outter.ApolloService;
import com.choice.cloud.architect.groot.util.CommonUtil;
import com.choice.driver.jwt.entity.AuthUser;
import com.ctrip.framework.apollo.openapi.dto.NamespaceReleaseDTO;
import com.ctrip.framework.apollo.openapi.dto.OpenItemDTO;
import com.ctrip.framework.apollo.openapi.dto.OpenNamespaceDTO;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class GrApolloChangeServiceImpl  extends BaseService  implements GrApolloChangeService {

    @Autowired
    private GrApolloChangeOrderMapper grApolloChangeOrderMapper;

    @Autowired
    private GrChangeRecodeMapper grChangeRecodeMapper;

    @Autowired
    private ApolloChangeConvertor apolloChangeConvertor;

    @Autowired
    private ApolloClientService apolloClientService;

    @Autowired
    private ApolloService apolloService;

    /**
     * 新增配置
     *
     * @param createApolloChangeRequest
     * @param authUser
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void createChange(CreateApolloChangeRequest createApolloChangeRequest, AuthUser authUser) {
        String appId = createApolloChangeRequest.getApolloAppCode();
        @NotEmpty(message = "环境名称不能为空") String env = createApolloChangeRequest.getEnv();
        @NotEmpty(message = "集群名称不能为空") String cluster = createApolloChangeRequest.getCluster();
        String changeId = createApolloChangeRequest.getChangeId();

        // 1、判断阿波罗配置中心key值是否存在 存在抛异常
        if (this.isExistsInApollo(appId, env, cluster, createApolloChangeRequest.getChangeItemKey())) {
            throw new ServiceException(ApolloResponseCode.DUPLICATE_KEY_APOLLO);
        }
        // 2、向apollo写k-v数据
        // 调用apollo openapi插入键值对，同时插入变更日志（gr_change_record）
        OpenItemDTO itemDTO = new OpenItemDTO();
        itemDTO.setDataChangeCreatedBy("apollo");
        itemDTO.setKey(createApolloChangeRequest.getChangeItemKey());
        itemDTO.setValue(createApolloChangeRequest.getChangeItemValue());
        itemDTO.setComment(createApolloChangeRequest.getDescription());
        itemDTO.setDataChangeCreatedTime(new Date());
        try {
            // 3、调用Apollo创建配置项
            OpenItemDTO resultItem = apolloClientService.createItem(appId, env, cluster, ApolloConstant.NAMESPACE_NAME_APPLICATION, itemDTO);
            // 4、记录Apollo变更日志
            this.insertChangeLog(changeId, appId,
                env,cluster, ApolloNamespaceEnum.APPLICATION.name().toLowerCase(),
                "","",createApolloChangeRequest.getChangeItemKey(),createApolloChangeRequest.getChangeItemValue(),
                ApolloChangeLogConstant.ADD_ITEM_LOG, ChangeOptEnum.ADD, OperateResultEnum.SUCCESS);
        }catch (RuntimeException e) {
            // 记录Apollo变更日志
            this.insertChangeLog(changeId, appId,
                env,cluster, ApolloNamespaceEnum.APPLICATION.name().toLowerCase(),"","",
                createApolloChangeRequest.getChangeItemKey(),createApolloChangeRequest.getChangeItemValue(),
                ApolloChangeLogConstant.ADD_ITEM_LOG, ChangeOptEnum.ADD, OperateResultEnum.FAILURE);

            log.error("阿波罗变更单创建异常：msg {}",e.getMessage());
            throw new ServiceException(e.getMessage());
        }

    }

    /**
     * 修改配置项。不存在就会创建
     *
     * @param updateApolloChangeRequest
     * @param authUser
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateOrCreateChange(UpdateApolloChangeRequest updateApolloChangeRequest, AuthUser authUser) {
        String appId = updateApolloChangeRequest.getApolloAppCode();
        @NotEmpty(message = "环境名称不能为空") String env = updateApolloChangeRequest.getEnv();
        @NotEmpty(message = "集群名称不能为空") String cluster = updateApolloChangeRequest.getCluster();
        String changeId = updateApolloChangeRequest.getChangeId();
        GrApolloChangeOrder grApolloChangeOrder = apolloChangeConvertor.updateRequest2ApolloChangeOrder(updateApolloChangeRequest);

        // 这里去查询阿波罗配置中心
        OpenItemDTO openItem = apolloClientService.getOpenItemByKey(appId, env, cluster, updateApolloChangeRequest.getChangeItemKey());
        Optional.ofNullable(openItem).orElseThrow(() -> new ServiceException("阿波罗配置项不存在或状态为已删除"));

        // 判断阿波罗配置中心key值是否存在 存在 修改
        if (this.isExistsInApollo(appId, env, cluster, updateApolloChangeRequest.getChangeItemKey())) {
            // 2.2 向apollo写k-v数据
            OpenItemDTO itemDTO = new OpenItemDTO();
            itemDTO.setDataChangeLastModifiedBy("apollo");
            itemDTO.setKey(updateApolloChangeRequest.getChangeItemKey());
            itemDTO.setValue(updateApolloChangeRequest.getChangeItemValue());
            itemDTO.setComment(updateApolloChangeRequest.getDescription());
            itemDTO.setDataChangeLastModifiedTime(new Date());
            try {
                if (!openItem.getKey().equals(updateApolloChangeRequest.getChangeItemKey())) {
                    apolloClientService.removeItem(appId, env, cluster, ApolloConstant.NAMESPACE_NAME_APPLICATION, openItem.getKey(), "apollo");
                    apolloClientService.createItem(appId, env, cluster, ApolloConstant.NAMESPACE_NAME_APPLICATION, itemDTO);
                } else {
                    apolloClientService.updateItem(appId, env, cluster, ApolloConstant.NAMESPACE_NAME_APPLICATION, itemDTO);
                }
                // 记录操作日志
                this.insertChangeLog(changeId, appId, env, cluster, ApolloNamespaceEnum.APPLICATION.name().toLowerCase(), openItem.getKey(), openItem.getValue(),
                    updateApolloChangeRequest.getChangeItemKey(), updateApolloChangeRequest.getChangeItemValue(), ApolloChangeLogConstant.UPDATE_ITEM_LOG, ChangeOptEnum.UPDATE,
                    OperateResultEnum.SUCCESS);
            } catch (RuntimeException e) {
                // 记录操作日志
                this.insertChangeLog(changeId, appId, env, cluster, ApolloNamespaceEnum.APPLICATION.name().toLowerCase(), openItem.getKey(), openItem.getValue(),
                    updateApolloChangeRequest.getChangeItemKey(), updateApolloChangeRequest.getChangeItemValue(), ApolloChangeLogConstant.UPDATE_ITEM_LOG, ChangeOptEnum.UPDATE,
                    OperateResultEnum.FAILURE);
                log.error("阿波罗变更单创建异常：msg {}", e.getMessage());
                throw new ServiceException(e.getMessage());
            }
            return;
        }

        // 判断阿波罗配置中心key值是否存在 不存在 新增
        // 2.2 向apollo写k-v数据
        OpenItemDTO itemDTO = new OpenItemDTO();
        itemDTO.setDataChangeCreatedBy("apollo");
        itemDTO.setKey(grApolloChangeOrder.getChangeItemKey());
        itemDTO.setValue(grApolloChangeOrder.getChangeItemValue());
        itemDTO.setComment(grApolloChangeOrder.getDescription());
        itemDTO.setDataChangeCreatedTime(new Date());
        try {
            if (!openItem.getKey().equals(updateApolloChangeRequest.getChangeItemKey())) {
                apolloClientService.removeItem(appId, env, cluster, ApolloConstant.NAMESPACE_NAME_APPLICATION, openItem.getKey(), "apollo");
            }
            OpenItemDTO resultItem = apolloClientService.createItem(appId, env, cluster, ApolloConstant.NAMESPACE_NAME_APPLICATION, itemDTO);
            // 记录操作日志
            this.insertChangeLog(changeId, appId, env, cluster, ApolloNamespaceEnum.APPLICATION.name().toLowerCase(), openItem.getKey(), openItem.getValue(),
                updateApolloChangeRequest.getChangeItemKey(), updateApolloChangeRequest.getChangeItemValue(), ApolloChangeLogConstant.UPDATE_ITEM_LOG, ChangeOptEnum.UPDATE,
                OperateResultEnum.SUCCESS);
        } catch (RuntimeException e) {
            // 记录操作日志
            this.insertChangeLog(changeId, appId, env, cluster, ApolloNamespaceEnum.APPLICATION.name().toLowerCase(), openItem.getKey(), openItem.getValue(),
                updateApolloChangeRequest.getChangeItemKey(), updateApolloChangeRequest.getChangeItemValue(), ApolloChangeLogConstant.UPDATE_ITEM_LOG, ChangeOptEnum.UPDATE,
                OperateResultEnum.FAILURE);
            log.error("阿波罗变更单创建异常：msg {}", e.getMessage());
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public GrApolloChangeOrder queryChange(String oid) {
        GrApolloChangeOrder grApolloChangeOrder = grApolloChangeOrderMapper.selectById(oid);
        return grApolloChangeOrder;
    }

    /**
     * 删除Apollo配置项
     *
     * @param deleteApolloChangeRequest
     * @param authUser
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteChange(DeleteApolloChangeRequest deleteApolloChangeRequest, AuthUser authUser) {
        String appId = deleteApolloChangeRequest.getApolloAppCode();
        @NotEmpty(message = "环境名称不能为空") String env = deleteApolloChangeRequest.getEnv();
        @NotEmpty(message = "集群名称不能为空") String cluster = deleteApolloChangeRequest.getCluster();
        String changeId = deleteApolloChangeRequest.getChangeId();
        @NotEmpty(message = "apollo变更子项key值不能为空") String itemKey = deleteApolloChangeRequest.getChangeItemKey();

        OpenItemDTO openItem = apolloClientService.getOpenItemByKey(appId, env, cluster, itemKey);
        Optional.ofNullable(openItem).orElseThrow(() -> new ServiceException("阿波罗配置项不存在或已删除"));

        // 是
        if (this.isExistsInApollo(appId, env, cluster, deleteApolloChangeRequest.getChangeItemKey())) {
            try {
                apolloClientService.removeItem(appId, env, cluster, ApolloConstant.NAMESPACE_NAME_APPLICATION, deleteApolloChangeRequest.getChangeItemKey(), "apollo");
                // 记录操作日志
                this.insertChangeLog(changeId, appId, env, cluster, ApolloNamespaceEnum.APPLICATION.name().toLowerCase(), openItem.getKey(), openItem.getValue(), "",
                    "", ApolloChangeLogConstant.DELETE_ITEM_LOG, ChangeOptEnum.DELETE, OperateResultEnum.SUCCESS);
            } catch (RuntimeException e) {
                // 记录操作日志
                this.insertChangeLog(changeId, appId, env, cluster, ApolloNamespaceEnum.APPLICATION.name().toLowerCase(), openItem.getKey(), openItem.getValue(), "",
                    "", ApolloChangeLogConstant.DELETE_ITEM_LOG, ChangeOptEnum.DELETE, OperateResultEnum.FAILURE);
                log.error("阿波罗变更单删除异常：msg {}", e.getMessage());
                throw new ServiceException(ApolloResponseCode.ITEM_HAS_DELETED);
            }
        }
    }

    /**
     * 发布配置
     *
     * @param publishApolloChangeRequest
     * @param authUser
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void publishChange(PublishApolloChangeRequest publishApolloChangeRequest, AuthUser authUser) {
        String appId = publishApolloChangeRequest.getApolloAppCode();
        @NotEmpty(message = "环境名称不能为空") String env = publishApolloChangeRequest.getEnv();
        @NotEmpty(message = "集群名称不能为空") String cluster = publishApolloChangeRequest.getCluster();
        String changeId = publishApolloChangeRequest.getChangeId();

        NamespaceReleaseDTO releaseDTO = new NamespaceReleaseDTO();
        releaseDTO.setReleasedBy("apollo");
        releaseDTO.setReleaseTitle(publishApolloChangeRequest.getReleaseTitle());
        releaseDTO.setEmergencyPublish(true);
        releaseDTO.setReleaseComment(publishApolloChangeRequest.getReleaseComment());
        try {
            // 发布配置
            apolloClientService.publishNamespace(appId, env, cluster, ApolloConstant.NAMESPACE_NAME_APPLICATION, releaseDTO);
            // 记录日志
            this.insertChangeLog(changeId, appId, env, cluster, ApolloNamespaceEnum.APPLICATION.name().toLowerCase(), "", "", "", "", ApolloChangeLogConstant.PUBLISH_ITEM_LOG, ChangeOptEnum.PUBLISH,
                OperateResultEnum.SUCCESS);
        } catch (RuntimeException e) {
            // 记录日志
            this.insertChangeLog(changeId, appId, env, cluster, ApolloNamespaceEnum.APPLICATION.name().toLowerCase(), "", "", "", "", ApolloChangeLogConstant.PUBLISH_ITEM_LOG, ChangeOptEnum.PUBLISH,
                OperateResultEnum.FAILURE);
            log.error("阿波罗变更单创建异常：msg {}", e.getMessage());
            throw new ServiceException(e.getMessage());
        }
    }

    /**
     * 配置列表查询（非分页）
     *
     * @param listApolloChangeRequest
     * @param authUser
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<ListApolloChangeDTO> list(ListApolloChangeRequest listApolloChangeRequest, AuthUser authUser) {
        @NotEmpty(message = "应用名不能为空") String appCode = listApolloChangeRequest.getAppCode();
        @NotEmpty(message = "环境名不能为空") String env = listApolloChangeRequest.getEnv();
        @NotEmpty(message = "集群名称不能为空") String cluster = listApolloChangeRequest.getCluster();

        // 返回结果集合
        List<ListApolloChangeDTO> resultList = Lists.newArrayList();

        // 获取所有的配置项，不调用apollo openapi，因为返回的配置项集合，不包含状态字段。
        // 采用http调用Apollo接口
        // http://localhost:8070/apps/apollo-test/envs/DEV/clusters/default/namespaces/application
        // 获取Apollo配置项列表集合
        List<ApolloQueryAllItemByEnvIdcNamespaceResponseDTO> allResponseItemFilterList = this.getApolloItemList(appCode, env, cluster);

        // 模糊查询key
        List<ApolloQueryAllItemByEnvIdcNamespaceResponseDTO> containsKeyList = Lists.newArrayList();
        if (StringUtils.isNotBlank(listApolloChangeRequest.getChangeItemKey())) {
            for (ApolloQueryAllItemByEnvIdcNamespaceResponseDTO responseDTO : allResponseItemFilterList) {
                // 过滤掉key和value都为空的情况。因为Apollo在该接口中按照每行数据返回的，如果配到注释的地方，就会出现key和value
                // 都为空的情况
                if (StringUtils.isBlank(responseDTO.getItem().getKey()) && StringUtils.isBlank(responseDTO.getItem().getValue())) {
                    continue;
                }
                if (responseDTO.getItem().getKey().contains(listApolloChangeRequest.getChangeItemKey())) {
                    containsKeyList.add(responseDTO);
                }
            }
        } else {
            containsKeyList = allResponseItemFilterList;
        }

        for (ApolloQueryAllItemByEnvIdcNamespaceResponseDTO responseDTO : containsKeyList) {
            // 获取Apollo配置项变更类型
            Integer apolloPublishType = this.getApolloChangeType(responseDTO);

            ListApolloChangeDTO apolloChangeDTO = ListApolloChangeDTO.builder()
                .changeItemKey(responseDTO.getItem().getKey())
                .changeItemValue(responseDTO.getItem().getValue())
                .description(responseDTO.getItem().getComment())
                .isDeleted(responseDTO.getIsDeleted())
                .isModified(responseDTO.getIsModified())
                .oldValue(responseDTO.getOldValue())
                .apolloAppCode(appCode)
                .env(env)
                .cluster(cluster)
                .status(!responseDTO.getIsDeleted() && !responseDTO.getIsModified() ? ApolloItemStatusEnum.PUBLISH_ED.getCode()
                    : ApolloItemStatusEnum.WAITING_PUBLISH.getCode())
                .publishType(apolloPublishType)
                .build();
            resultList.add(apolloChangeDTO);
        }

        return resultList;
    }

    /**
     * 获取Apollo配置项变更类型
     * @param responseDTO
     * @return
     */
    private Integer getApolloChangeType(ApolloQueryAllItemByEnvIdcNamespaceResponseDTO responseDTO) {
        Integer apolloPublishType = ApolloPublishTypeEnum.NO_CHANGE_ITEM.getCode();
        if (responseDTO.getIsDeleted()) {
            apolloPublishType = ApolloPublishTypeEnum.DELETE_ITEM.getCode();
        } else if (responseDTO.getIsModified() && StringUtils.isBlank(responseDTO.getOldValue())) {
            apolloPublishType = ApolloPublishTypeEnum.ADD_ITEM.getCode();
        } else if (responseDTO.getIsModified() && StringUtils.isNotBlank(responseDTO.getOldValue())) {
            apolloPublishType = ApolloPublishTypeEnum.UPDATE_ITEM.getCode();
        }
        return apolloPublishType;
    }

    /**
     * 获取Apollo配置项列表集合
     * @param appCode
     * @param env
     * @param cluster
     * @return
     */
    private List<ApolloQueryAllItemByEnvIdcNamespaceResponseDTO> getApolloItemList(@NotEmpty(message = "应用名不能为空") String appCode,
        @NotEmpty(message = "环境名不能为空") String env, @NotEmpty(message = "集群名称不能为空") String cluster) {

        // 返回结果，已过滤掉key和value都为空的情况
        List<ApolloQueryAllItemByEnvIdcNamespaceResponseDTO> allResponseItemFilterList = Lists.newArrayList();

        // 根据环境获取portal地址
        String portalUrl = this.getPortalUrlByEnv(env);

        log.info("根据环境、集群和命名空间远程调用Apollo接口入参：portal:{}，appCode:{}，env:{}，idc:{}", portalUrl, appCode, env, cluster);
        ApolloQueryAllItemByEnvIdcNamespaceResponseDTO apolloResponseDTO = apolloService.queryAllItemByEnvIdcNamespace(portalUrl, appCode, env, cluster,
            ApolloConstant.NAMESPACE_NAME_APPLICATION);
        log.info("根据环境、集群和命名空间远程调用Apollo接口返参：{}", JSON.toJSONString(apolloResponseDTO));

        if (Objects.isNull(apolloResponseDTO) || CollectionUtils.isEmpty(apolloResponseDTO.getItems())) {
            return allResponseItemFilterList;
        }

        List<ApolloQueryAllItemByEnvIdcNamespaceResponseDTO> allResponseItemList = apolloResponseDTO.getItems();
        for (ApolloQueryAllItemByEnvIdcNamespaceResponseDTO responseDTO : allResponseItemList) {
            // 过滤掉key和value都为空的情况。因为Apollo在该接口中按照每行数据返回的，如果配到注释的地方，就会出现key和value
            // 都为空的情况
            if (StringUtils.isBlank(responseDTO.getItem().getKey()) && StringUtils.isBlank(responseDTO.getItem().getValue())) {
                continue;
            }
            allResponseItemFilterList.add(responseDTO);
        }
        return allResponseItemFilterList;
    }

    /**
     * 根据环境获取portal地址
     * @param env
     * @return
     */
    private String getPortalUrlByEnv(String env) {
        if (StringUtils.isBlank(env)) {
            throw new ServiceException(ApolloResponseCode.ENV_NOT_BLANK);
        }
        if (ApolloEnvAndIdcEnum.DEV.getEnv().equalsIgnoreCase(env)
            || ApolloEnvAndIdcEnum.FAT.getEnv().equalsIgnoreCase(env)) {
            return ApolloConstant.PORTAL_URL_OFFLINE;
        } else if (ApolloEnvAndIdcEnum.PRE.getEnv().equalsIgnoreCase(env)
            || ApolloEnvAndIdcEnum.PRO.getEnv().equalsIgnoreCase(env)) {
            return ApolloConstant.PORTAL_URL_ONLINE;
        } else {
            throw new ServiceException(ApolloResponseCode.ENV_NOT_BLANK);
        }
    }

    private void insertApolloChange(List<ListApolloChangeDTO> apolloKeys,List<String> dbKeyList,ListApolloChangeRequest listApolloChangeRequest, AuthUser authUser){
        List<GrApolloChangeOrder> list = Lists.newArrayList();
        for (ListApolloChangeDTO apolloChangeDTO:apolloKeys) {
            String changeItemKey = apolloChangeDTO.getChangeItemKey();
            if(!dbKeyList.contains(changeItemKey)){
                GrApolloChangeOrder apolloChangeOrder = new GrApolloChangeOrder();
                initSaveModel(apolloChangeOrder, authUser);
                apolloChangeOrder.setAppId(listApolloChangeRequest.getAppCode());
                apolloChangeOrder.setEnv(listApolloChangeRequest.getEnv());
                apolloChangeOrder.setCluster(listApolloChangeRequest.getCluster());
                apolloChangeOrder.setChangeItemKey(apolloChangeDTO.getChangeItemKey());
                apolloChangeOrder.setChangeItemValue(apolloChangeDTO.getChangeItemValue());
                apolloChangeOrder.setDescription(apolloChangeDTO.getDescription());
                apolloChangeOrder.setNamespace(ApolloNamespaceEnum.APPLICATION.name());
                apolloChangeOrder.setStatus(GlobalConst.PUBLISHED);
                list.add(apolloChangeOrder);
            }
        }
        if(list.isEmpty()) return;
        grApolloChangeOrderMapper.batchInsert(list);
    }

    private List<?> page(Integer page,Integer size,List<?> list){
        //分页
        Integer totalNum = list.size();
        //默认从零分页，这里要考虑这种情况，下面要计算。
        int pageNum = page + 1;
        int pageSize = size;
        Integer totalPage = 0;
        if (totalNum > 0) {
            totalPage = totalNum % pageSize == 0 ? totalNum / pageSize : totalNum / pageSize + 1;
        }
        if (pageNum > totalPage) {
            pageNum = totalPage;
        }
        int startPoint = (pageNum - 1) * pageSize;
        int endPoint = startPoint + pageSize;
        if (totalNum <= endPoint) {
            endPoint = totalNum;
        }
        return list.subList(startPoint, endPoint);
    }




    private List<ListApolloChangeDTO> getApolloKeys(String appCode,String env,String cluster){
        List<ListApolloChangeDTO> resultList = Lists.newArrayList();
        OpenNamespaceDTO clusterAllNamespaces = apolloClientService.getClusterAllNamespaces(appCode, env, cluster, ApolloNamespaceEnum.APPLICATION.name());
        List<OpenItemDTO> itemList = clusterAllNamespaces.getItems();
        itemList = Optional.ofNullable(itemList).orElse(new ArrayList<OpenItemDTO>());
        for (OpenItemDTO openItemDTO:itemList) {
            if("".equals(openItemDTO.getKey())) continue;
            String key = openItemDTO.getKey();
            String value = openItemDTO.getValue();
            String comment = openItemDTO.getComment();
            ListApolloChangeDTO build = ListApolloChangeDTO
                    .builder()
                    .env(env)
                    .cluster(cluster)
                    .apolloAppCode(appCode)
                    .changeItemKey(key)
                    .changeItemValue(value)
                    .description(comment)
                    .status(GlobalConst.ENABLE)
                    .build();
            resultList.add(build);
        }
        return resultList;
    }

    private void insertChangeLog(String changeId, String appId, String env, String cluster, String namespace, String oldKey, String oldValue, String newKey, String newValue, String desc, ChangeOptEnum changeOptEnum, OperateResultEnum operateResultEnum){
        String odValue = StringUtils.isEmpty(oldKey)?"":oldKey + " = " + oldValue;
        String neValue = StringUtils.isEmpty(newKey)?"":newKey + " = " + newValue;
        //变更日志
        GrChangeRecode build = GrChangeRecode.builder()
                .oid(CommonUtil.getUUIdUseMongo())
                .changeId(changeId)
                .createTime(LocalDateTime.now())
                .createUser(CommonUtil.currentUserId())
                .deleteFlag(GlobalConst.NOT_DELETE)
                .status(GlobalConst.ENABLE)
                .appId(appId)
                .env(env)
                .cluster(cluster)
                .namespace(namespace)
                .logType(ChangeLogTypeEnum.APOLLO.name())
                .optType(changeOptEnum.name())
                .oldValue(odValue)
                .newValue(neValue)
                .operator(CommonUtil.currentUserId())
                .optResult(operateResultEnum.value())
                .optTime(LocalDateTime.now())
                .optDetail(desc)
                .build();
        grChangeRecodeMapper.insert(build);
    }

    /**
     * 判断指定的key是否存在Apollo中
     * @param appId
     * @param env
     * @param cluster
     * @param key
     * @return
     */
    private boolean isExistsInApollo(String appId, String env, String cluster, String key) {
        OpenNamespaceDTO application = apolloClientService.getClusterAllNamespaces(appId, env, cluster, ApolloConstant.NAMESPACE_NAME_APPLICATION);
        if (null == application) {
            throw new ServiceException("阿波罗集群不存在");
        }
        List<OpenItemDTO> itemsList = application.getItems();
        Map<String, List<OpenItemDTO>> itemsMap = itemsList.stream().collect(Collectors.groupingBy(OpenItemDTO::getKey));
        return itemsMap.containsKey(key);
    }

    /**
     * 阿波罗修改的配置项列表集合
     *
     * @param request
     * @return
     */
    @Override
    public List<ListApolloUpdateItemResponseDTO> listUpdateItem(ListApolloUpdateItemRequestDTO request) {
        @NotBlank(message = "应用名不能为空") String appCode = request.getAppCode();
        @NotBlank(message = "环境名不能为空") String env = request.getEnv();
        @NotBlank(message = "集群名称不能为空") String cluster = request.getCluster();

        // 返回结果集合
        List<ListApolloUpdateItemResponseDTO> resultList = Lists.newArrayList();

        // 获取Apollo配置项集合
        List<ApolloQueryAllItemByEnvIdcNamespaceResponseDTO> allResponseItemFilterList = this.getApolloItemList(appCode, env, cluster);

        if (CollectionUtils.isEmpty(allResponseItemFilterList)) {
            return resultList;
        }

        for (ApolloQueryAllItemByEnvIdcNamespaceResponseDTO responseDTO : allResponseItemFilterList) {
            // 获取Apollo配置项变更类型
            Integer apolloPublishType = this.getApolloChangeType(responseDTO);

            ApolloQueryItemDTO item = responseDTO.getItem();
            if (responseDTO.getIsDeleted() || responseDTO.getIsModified()) {
                ListApolloUpdateItemResponseDTO resultDTO = ListApolloUpdateItemResponseDTO.builder()
                    .changeItemKey(item.getKey())
                    .oldValue(responseDTO.getOldValue())
                    .changeItemValue(item.getValue())
                    .changeType(apolloPublishType)
                    .build();
                resultList.add(resultDTO);
            }
        }

        return resultList;
    }

    /**
     * 分页查询阿波罗配置项更改历史
     *
     * @param request
     * @return
     */
    @Override
    public List<PageListApolloCommitHistoryResponseDTO> pageListCommitHistoryItem(PageListApolloCommitHistoryRequestDTO request) {
        @NotBlank(message = "应用名不能为空") String appCode = request.getAppCode();
        @NotBlank(message = "环境名不能为空") String env = request.getEnv();
        @NotBlank(message = "集群名称不能为空") String cluster = request.getCluster();
        Integer pageNum = 0;
        Integer pageSize = 100;

        // 返回结果集合
        List<PageListApolloCommitHistoryResponseDTO> resultList = Lists.newArrayList();

        // 根据环境获取portal地址
        String portalUrl = this.getPortalUrlByEnv(env);

        log.info("分页查询应用的某个环境集群命名空间下的已更改历史配置信息远程调用Apollo接口入参：portal:{}，appCode:{}，env:{}，idc:{}，pageNum:{}，pageSize:{}", portalUrl, appCode, env, cluster, pageNum, pageSize);
        List<ApolloPageQueryCommitItemHistoryListResponseDTO> apolloResponseDTOList = apolloService.pageQueryCommitItemHistoryList(portalUrl, appCode, env, cluster,
            ApolloConstant.NAMESPACE_NAME_APPLICATION, pageNum, pageSize);
        log.info("分页查询应用的某个环境集群命名空间下的已更改历史配置信息远程调用Apollo接口返参：{}", JSON.toJSONString(apolloResponseDTOList));

        if (CollectionUtils.isEmpty(apolloResponseDTOList)) {
            return resultList;
        }

        for (ApolloPageQueryCommitItemHistoryListResponseDTO apolloResponseDTO : apolloResponseDTOList) {
            PageListApolloCommitHistoryResponseDTO resultDTO = new PageListApolloCommitHistoryResponseDTO();
            /**
             * 设置返回结果到集合
             */
            this.setResultDto2List(resultList, apolloResponseDTO, resultDTO);
        }

        return resultList;
    }

    /**
     * 设置返回结果到集合
     */
    private void setResultDto2List(List<PageListApolloCommitHistoryResponseDTO> resultList, ApolloPageQueryCommitItemHistoryListResponseDTO apolloResponseDTO,
        PageListApolloCommitHistoryResponseDTO resultDTO) {
        if (CollectionUtils.isNotEmpty(apolloResponseDTO.getCreateItems())) {
            CreateItemsBean createItemsBean = apolloResponseDTO.getCreateItems().get(0);
            resultDTO.setTypeName(ApolloCommitHistoryTypeEnum.CREATE.getDesc());
            resultDTO.setKey(createItemsBean.getKey());
            resultDTO.setOldValue("");
            resultDTO.setNewValue(createItemsBean.getValue());
            resultDTO.setCommitTime(createItemsBean.getDataChangeLastModifiedTime());

            resultList.add(resultDTO);
        } else if (CollectionUtils.isNotEmpty(apolloResponseDTO.getUpdateItems())) {
            UpdateItemsBean updateItemsBean = apolloResponseDTO.getUpdateItems().get(0);
            OldItemBean oldItem = updateItemsBean.getOldItem();
            NewItemBean newItem = updateItemsBean.getNewItem();
            resultDTO.setTypeName(ApolloCommitHistoryTypeEnum.UPDATE.getDesc());
            resultDTO.setKey(oldItem.getKey());
            resultDTO.setOldValue(oldItem.getValue());
            resultDTO.setNewValue(newItem.getValue());
            resultDTO.setCommitTime(newItem.getDataChangeLastModifiedTime());

            resultList.add(resultDTO);
        } else if (CollectionUtils.isNotEmpty(apolloResponseDTO.getDeleteItems())) {
            DeleteItemsBean deleteItemsBean = apolloResponseDTO.getDeleteItems().get(0);
            resultDTO.setTypeName(ApolloCommitHistoryTypeEnum.DELETE.getDesc());
            resultDTO.setKey(deleteItemsBean.getKey());
            resultDTO.setOldValue(deleteItemsBean.getValue());
            resultDTO.setNewValue("");
            resultDTO.setCommitTime(deleteItemsBean.getDataChangeLastModifiedTime());

            resultList.add(resultDTO);
        }
    }
}
