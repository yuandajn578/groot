package com.choice.cloud.architect.groot.controller;

import com.choice.cloud.architect.groot.convertor.ApolloClientConvertor;
import com.choice.cloud.architect.groot.dto.apollo.ApolloBasicDTO;
import com.choice.cloud.architect.groot.dto.apollo.ApolloCompareRequestDTO;
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
import java.util.Map;

/**
 * 阿波罗api
 */
@Api(description = "阿波罗工具类",tags = "阿波罗工具类")
@RestController
@RequestMapping("/api/apollo/tool")
public class ApolloToolController {

    @Autowired
    private ApolloClientService apolloClientService;

    @Autowired
    private ApolloClientConvertor apolloClientConvertor;

    @ApiOperation(value = "对比两个cluster",notes = "对比两个cluster")
    @PostMapping("/compareCluster")
    public ResponseData<List<String>> compareCluster(@RequestBody ApolloCompareRequestDTO request) {
        List<String> ret = apolloClientService.compareCluster(request);
        return ResponseData.createBySuccess(ret);
    }

    @ApiOperation(value = "获取某应用的配置，包括公共配置",notes = "获取某应用的配置")
    @PostMapping("/queryClusterAllItem")
    public ResponseData<Map<String, String>> queryClusterAllItem(@RequestBody ApolloBasicDTO request) {
        Map<String, String> ret = apolloClientService.findAllItem(request);
        return ResponseData.createBySuccess(ret);
    }

}
