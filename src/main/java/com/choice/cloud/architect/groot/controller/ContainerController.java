package com.choice.cloud.architect.groot.controller;

import com.choice.cloud.architect.groot.dto.LdcPodInfoDTO;
import com.choice.cloud.architect.groot.dto.ListEcsInfoDTO;
import com.choice.cloud.architect.groot.dto.ListNamespaceDetailDTO;
import com.choice.cloud.architect.groot.dto.ListPodInfoDTO;
import com.choice.cloud.architect.groot.properties.IdcLdcOrchestrationProperties;
import com.choice.cloud.architect.groot.request.*;
import com.choice.cloud.architect.groot.response.ResponseData;
import com.choice.cloud.architect.groot.service.inner.ContainerManageService;
import com.choice.driver.jwt.entity.AuthUser;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

/**
 * @author zhangkun
 */
@Api(value = "容器管理", tags = "容器管理", description = "容器管理")
@RestController
@RequestMapping(value = "/api/container")
@Slf4j
public class ContainerController {
    @Autowired
    private ContainerManageService containerManageService;

    @Autowired
    private IdcLdcOrchestrationProperties idcLdcOrchestrationProperties;

    /**
     * 获取集群下所有的namespace
     */
    @ApiOperation(value = "查询应用列表")
    @PostMapping(value = "/list")
    public ResponseData<List<ListNamespaceDetailDTO>> listNamespaceDetail(@Valid @RequestBody QueryNamespaceDetailRequest request, AuthUser authUser) {
        List<ListNamespaceDetailDTO> listNamespaceDetailDTOS = containerManageService.listNamespaceDetail(request, authUser);
        return ResponseData.createBySuccess(listNamespaceDetailDTOS);
    }

    /**
     * pod列表
     */
    @ApiOperation(value = "查询某个应用下的Pod列表")
    @PostMapping(value = "/podList")
    public ResponseData<List<ListPodInfoDTO>> podList(@Valid @RequestBody QueryPodListRequest request) {
        List<ListPodInfoDTO> podList = containerManageService.podList(request);
        return ResponseData.createBySuccess(podList);
    }

    /**
     * 环境分组列表
     */
    @ApiOperation(value = "环境分组列表")
    @GetMapping(value = "/namespace/list")
    public ResponseData<List<String>> getNsNameList(AuthUser authUser) {
        List<String> nsNameList = containerManageService.getNsNameList(authUser);
        return ResponseData.createBySuccess(nsNameList);
    }

    /**
     * 扩缩容
     */
    @ApiOperation(value = "扩缩容")
    @GetMapping(value = "/updateReplicas")
    public ResponseData<List<String>> updateReplicas(@RequestBody UpdateReplicasRequest request) {
        containerManageService.updateReplicas(request.getEnvType().concat("-").concat(request.getEnvCode()),
                request.getAppCode(), request.getReplicas());
        return ResponseData.createBySuccess();
    }

    @PostMapping("/delete")
    public ResponseData delete(@RequestBody KubernetesDeleteRequest request){
        containerManageService.delete(request);
        return ResponseData.createBySuccess();
    }

    @ApiOperation(value = "快速回滚到上一个版本")
    @PostMapping("/rollback/previous")
    public ResponseData rollbackPrevious(@Valid @RequestBody DeploymentRollbackPreviousRequest request) {
        containerManageService.rollbackToPreviousRevision(request.getEnvType(), request.getEnvCode(), request.getAppCode());

        return ResponseData.createBySuccess();
    }

    /**
     * ldc pod列表
     */
    @ApiOperation(value = "查询某个服务分组下的Pod列表")
    @PostMapping(value = "/listLdcPodInfo")
    public ResponseData<List<LdcPodInfoDTO>> listLdcPodInfo(@Valid @RequestBody QueryLdcPodListRequest request) {
        List<LdcPodInfoDTO> ldcPodInfoDTOS = containerManageService.listLdcPodInfo(request);
        return ResponseData.createBySuccess(ldcPodInfoDTOS);
    }

    @ApiOperation(value = "删除StatefulSet类型的Pods")
    @PostMapping(value = "/deleteStatefulSetPods")
    public ResponseData deleteStatefulSetPods(@Valid @RequestBody DeleteStatefulSetPodsRequest request, AuthUser authUser) {
        containerManageService.deleteStatefulSetPods(request.getEnvType(), request.getEnvCode(), request.getPodName(), authUser);
        return ResponseData.createBySuccess();
    }

    @ApiOperation(value = "查询Node列表")
    @PostMapping(value = "/nodes")
    public ResponseData<List<ListEcsInfoDTO>> listNodes(@RequestBody ListNodeInfoRequest request) {
        if (StringUtils.isBlank(request.getIdc()) && StringUtils.isBlank(request.getLdc())) {
            List<ListEcsInfoDTO> listEcsInfoList = Lists.newArrayList();

            Set<String> idcSet = Sets.newHashSet();
            idcSet.addAll(idcLdcOrchestrationProperties.getOfflineIdc());
            idcSet.addAll(idcLdcOrchestrationProperties.getOnlineIdc());

            for (String idc : idcSet) {
                request.setIdc(idc);
                listEcsInfoList.addAll(containerManageService.listNodeInfo(request));
            }


            log.info("/api/container/nodes, listEcsInfoList => {}", listEcsInfoList);
            return ResponseData.createBySuccess(listEcsInfoList);
        }
        return ResponseData.createBySuccess(containerManageService.listNodeInfo(request));
    }

    @ApiOperation(value = "根据podName批量Pod信息")
    @PostMapping(value = "/batchQueryPodByName")
    public ResponseData<List<ListPodInfoDTO>> batchQueryPodByName(@RequestBody BatchQueryPodRequest request) {
        return ResponseData.createBySuccess(containerManageService.batchQueryPodByName(request));
    }
}
