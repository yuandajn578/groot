package com.choice.cloud.architect.groot.controller;

import com.choice.cloud.architect.groot.convertor.ApolloClientConvertor;
import com.choice.cloud.architect.groot.request.ApolloAppNamespaceRequest;
import com.choice.cloud.architect.groot.request.ApolloItemRequest;
import com.choice.cloud.architect.groot.request.ApolloNamespaceReleaseRequest;
import com.choice.cloud.architect.groot.response.ResponseData;
import com.choice.cloud.architect.groot.service.inner.ApolloClientService;
import com.ctrip.framework.apollo.openapi.dto.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 阿波罗api
 */
@Api(description = "阿波罗api",tags = "阿波罗api")
@RestController
@RequestMapping("/api/apollo")
public class ApolloClientController {

    @Autowired
    private ApolloClientService apolloClientService;

    @Autowired
    private ApolloClientConvertor apolloClientConvertor;


    @ApiOperation(value = "获取应用集群线上和线下",notes = "获取应用集群线上和线下")
    @ApiImplicitParam(name="appId",value = "应用ID",dataType = "String",paramType = "Path",required = true)
    @GetMapping("/envClusters/{appId}")
    public ResponseData<List<OpenEnvClusterDTO>> getAllEnvClusters(@PathVariable("appId") String appId) {
        List<OpenEnvClusterDTO> allEnvClusters = apolloClientService.getAllEnvClusters(appId);
        return ResponseData.createBySuccess(allEnvClusters);
    }


    @ApiOperation(value = "获取所有集群和namespace下配置信息",notes = "获取所有集群和namespace下配置信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="appId",value = "应用ID",dataType = "String",paramType = "Query",required = true),
            @ApiImplicitParam(name="env",value = "环境",dataType = "String",paramType = "Query",required = true),
            @ApiImplicitParam(name="clusterName",value = "集群名称",dataType = "String",paramType = "Query",required = true)
    })
    @PostMapping("/clusterNamespaces")
    public ResponseData<List<OpenNamespaceDTO>> getAllClusterNamespaces(@RequestParam("appId") String appId,
                                                          @RequestParam("env") String env,
                                                          @RequestParam("clusterName") String clusterName) {
        List<OpenNamespaceDTO> allClusterNamespaces = apolloClientService.getAllClusterNamespaces(appId, env, clusterName);
        return ResponseData.createBySuccess(allClusterNamespaces);
    }


    @ApiOperation(value = "获取指定集群和namespace下配置信息",notes = "获取指定集群和namespace下配置信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="appId",value = "应用ID",dataType = "String",paramType = "Query",required = true),
            @ApiImplicitParam(name="env",value = "环境",dataType = "String",paramType = "Query",required = true),
            @ApiImplicitParam(name="clusterName",value = "集群名称",dataType = "String",paramType = "Query",required = true),
            @ApiImplicitParam(name="namespaceName",value = "命名空间名称",dataType = "String",paramType = "Query",defaultValue = "application")
    })
    @PostMapping("/clusterAllNamespaces")
    public ResponseData<OpenNamespaceDTO> getClusterAllNamespaces(@RequestParam("appId") String appId,
                                                    @RequestParam("env") String env,
                                                    @RequestParam("clusterName") String clusterName,
                                                    @RequestParam(value = "namespaceName",defaultValue = "application",required = false) String namespaceName) {
        OpenNamespaceDTO clusterAllNamespaces = apolloClientService.getClusterAllNamespaces(appId, env, clusterName, namespaceName);
        return ResponseData.createBySuccess(clusterAllNamespaces);
    }



    @ApiOperation(hidden = true,value = "创建命名空间",notes = "创建命名空间")
    @PostMapping("/createAppNamespace")
    public ResponseData<OpenAppNamespaceDTO> createAppNamespace(@RequestBody ApolloAppNamespaceRequest apolloAppNamespaceRequest) {
        OpenAppNamespaceDTO openAppNamespaceDTO = apolloClientConvertor.namespaceReq2AppNamespace(apolloAppNamespaceRequest);
        OpenAppNamespaceDTO appNamespace = apolloClientService.createAppNamespace(openAppNamespaceDTO);
        return ResponseData.createBySuccess(appNamespace);
    }


    @ApiOperation(value = "获取某个Namespace当前编辑人接口",notes = "获取某个Namespace当前编辑人接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name="appId",value = "应用ID",dataType = "String",paramType = "Query",required = true),
            @ApiImplicitParam(name="env",value = "环境",dataType = "String",paramType = "Query",required = true),
            @ApiImplicitParam(name="clusterName",value = "集群名称",dataType = "String",paramType = "Query",required = true),
            @ApiImplicitParam(name="namespaceName",value = "命名空间名称",dataType = "String",paramType = "Query",defaultValue = "application"),
    })
    @PostMapping("/namespaceLock")
    public ResponseData<OpenNamespaceLockDTO> getNamespaceLock(@RequestParam("appId") String appId,
                                                 @RequestParam("env") String env,
                                                 @RequestParam("clusterName") String clusterName,
                                                 @RequestParam(value = "namespaceName",defaultValue = "application",required = false) String namespaceName) {
        OpenNamespaceLockDTO namespaceLock = apolloClientService.getNamespaceLock(appId, env, clusterName, namespaceName);
        return ResponseData.createBySuccess(namespaceLock);
    }



    @ApiOperation(value = "新增配置项",notes = "新增配置项")
    @ApiImplicitParams({
            @ApiImplicitParam(name="appId",value = "应用ID",dataType = "String",paramType = "Query",required = true),
            @ApiImplicitParam(name="env",value = "环境",dataType = "String",paramType = "Query",required = true),
            @ApiImplicitParam(name="clusterName",value = "集群名称",dataType = "String",paramType = "Query",required = true),
            @ApiImplicitParam(name="namespaceName",value = "命名空间名称",dataType = "String",paramType = "Query",defaultValue = "application"),
    })
    @PostMapping("/createItem")
    public ResponseData<OpenItemDTO> createItem(@RequestParam("appId") String appId,
                                  @RequestParam("env")String env,
                                  @RequestParam("clusterName")String clusterName,
                                  @RequestParam(value = "namespaceName",defaultValue = "application",required = false)String namespaceName,
                                  @RequestBody ApolloItemRequest apolloItemRequest) {
        OpenItemDTO openItemDTO = apolloClientConvertor.itemReq2Item(apolloItemRequest);
        OpenItemDTO item = apolloClientService.createItem(appId, env, clusterName, namespaceName, openItemDTO);
        return ResponseData.createBySuccess(item);
    }



    @ApiOperation(value = "修改配置项",notes = "修改配置项")
    @ApiImplicitParams({
            @ApiImplicitParam(name="appId",value = "应用ID",dataType = "String",paramType = "Query",required = true),
            @ApiImplicitParam(name="env",value = "环境",dataType = "String",paramType = "Query",required = true),
            @ApiImplicitParam(name="clusterName",value = "集群名称",dataType = "String",paramType = "Query",required = true),
            @ApiImplicitParam(name="namespaceName",value = "命名空间名称",dataType = "String",paramType = "Query",defaultValue = "application"),
    })
    @PostMapping("/updateItem")
    public ResponseData updateItem(@RequestParam("appId") String appId,
                           @RequestParam("env")String env,
                           @RequestParam("clusterName")String clusterName,
                           @RequestParam(value = "namespaceName",defaultValue = "application",required = false)String namespaceName,
                           @RequestBody ApolloItemRequest apolloItemRequest) {
        OpenItemDTO openItemDTO = apolloClientConvertor.itemReq2Item(apolloItemRequest);
        apolloClientService.updateItem(appId,env,clusterName,namespaceName,openItemDTO);
        return ResponseData.createBySuccess();
    }



    @ApiOperation(value = "创建或修改配置项",notes = "创建或修改配置项")
    @ApiImplicitParams({
            @ApiImplicitParam(name="appId",value = "应用ID",dataType = "String",paramType = "Query",required = true),
            @ApiImplicitParam(name="env",value = "环境",dataType = "String",paramType = "Query",required = true),
            @ApiImplicitParam(name="clusterName",value = "集群名称",dataType = "String",paramType = "Query",required = true),
            @ApiImplicitParam(name="namespaceName",value = "命名空间名称",dataType = "String",paramType = "Query",defaultValue = "application"),
    })
    @PostMapping("/createOrUpdateItem")
    public ResponseData createOrUpdateItem(@RequestParam("appId") String appId,
                                   @RequestParam("env")String env,
                                   @RequestParam("clusterName")String clusterName,
                                   @RequestParam(value = "namespaceName",defaultValue = "application",required = false)String namespaceName,
                                   @RequestBody ApolloItemRequest apolloItemRequest) {
        OpenItemDTO openItemDTO = apolloClientConvertor.itemReq2Item(apolloItemRequest);
        apolloClientService.createOrUpdateItem(appId,env,clusterName,namespaceName,openItemDTO);
        return ResponseData.createBySuccess();
    }




    @ApiOperation(value = "删除配置项",notes = "删除配置项")
    @ApiImplicitParams({
            @ApiImplicitParam(name="appId",value = "应用ID",dataType = "String",paramType = "Query",required = true),
            @ApiImplicitParam(name="env",value = "环境",dataType = "String",paramType = "Query",required = true),
            @ApiImplicitParam(name="clusterName",value = "集群名称",dataType = "String",paramType = "Query",required = true),
            @ApiImplicitParam(name="namespaceName",value = "命名空间名称",dataType = "String",paramType = "Query",defaultValue = "application"),
            @ApiImplicitParam(name="key",value = "配置key",dataType = "String",paramType = "Query",required = true),
            @ApiImplicitParam(name="operator",value = "操作人",dataType = "String",paramType = "Query",required = true)
    })
    @PostMapping("/removeItem")
    public ResponseData removeItem(@RequestParam("appId") String appId,
                           @RequestParam("env")String env,
                           @RequestParam("clusterName")String clusterName,
                           @RequestParam(value = "namespaceName",defaultValue = "application",required = false)String namespaceName,
                           @RequestParam("key")String key,
                           @RequestParam("operator")String operator) {
        apolloClientService.removeItem(appId,env,clusterName,namespaceName,key,operator);
        return ResponseData.createBySuccess();
    }



    @ApiOperation(value = "发布配置",notes = "发布配置")
    @ApiImplicitParams({
            @ApiImplicitParam(name="appId",value = "应用ID",dataType = "String",paramType = "Query",required = true),
            @ApiImplicitParam(name="env",value = "环境",dataType = "String",paramType = "Query",required = true),
            @ApiImplicitParam(name="clusterName",value = "集群名称",dataType = "String",paramType = "Query",required = true),
            @ApiImplicitParam(name="namespaceName",value = "命名空间名称",dataType = "String",paramType = "Query",defaultValue = "application"),
    })
    @PostMapping("/publishNamespace")
    public ResponseData<OpenReleaseDTO> publishNamespace(@RequestParam("appId") String appId,
                                           @RequestParam("env")String env,
                                           @RequestParam("clusterName")String clusterName,
                                           @RequestParam(value = "namespaceName",defaultValue = "application",required = false)String namespaceName,
                                           @RequestBody ApolloNamespaceReleaseRequest apolloNamespaceReleaseRequest) {
        NamespaceReleaseDTO namespaceReleaseDTO = apolloClientConvertor.namespaceReleaseReq2NamespaceRelease(apolloNamespaceReleaseRequest);
        OpenReleaseDTO openReleaseDTO = apolloClientService.publishNamespace(appId, env, clusterName, namespaceName, namespaceReleaseDTO);
        return ResponseData.createBySuccess(openReleaseDTO);
    }



    @ApiOperation(value = "查询最近发布信息",notes = "查询最近发布信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="appId",value = "应用ID",dataType = "String",paramType = "Query",required = true),
            @ApiImplicitParam(name="env",value = "环境",dataType = "String",paramType = "Query",required = true),
            @ApiImplicitParam(name="clusterName",value = "集群名称",dataType = "String",paramType = "Query",required = true),
            @ApiImplicitParam(name="namespaceName",value = "命名空间名称",dataType = "String",paramType = "Query",defaultValue = "application"),
    })
    @PostMapping("/getLatestActiveRelease")
    public ResponseData<OpenReleaseDTO> getLatestActiveRelease(@RequestParam("appId") String appId,
                                                 @RequestParam("env")String env,
                                                 @RequestParam("clusterName")String clusterName,
                                                 @RequestParam(value = "namespaceName",defaultValue = "application",required = false)String namespaceName) {
        OpenReleaseDTO latestActiveRelease = apolloClientService.getLatestActiveRelease(appId, env, clusterName, namespaceName);
        return ResponseData.createBySuccess(latestActiveRelease);
    }
}
