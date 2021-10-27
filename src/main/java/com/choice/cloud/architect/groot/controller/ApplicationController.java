package com.choice.cloud.architect.groot.controller;

import com.choice.cloud.architect.groot.dto.ApplicationListByUserDTO;
import com.choice.cloud.architect.groot.option.WebPage;
import com.choice.cloud.architect.groot.remote.AppListRequest;
import com.choice.cloud.architect.groot.remote.AppListResponse;
import com.choice.cloud.architect.groot.remote.milkyway.MilkywayClient;
import com.choice.cloud.architect.groot.response.ResponseData;
import com.choice.cloud.architect.groot.service.inner.GrApplicationService;
import com.choice.driver.jwt.entity.AuthUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "应用管理", tags = "应用管理")
@RequestMapping("/application")
@RestController
@Slf4j
public class ApplicationController {

    @Autowired
    private MilkywayClient milkywayClient;

    @Autowired
    private GrApplicationService grApplicationService;

    @PostMapping("/list")
    public ResponseData appList(@RequestBody WebPage<AppListRequest> paramWebPage){
        ResponseData<WebPage<List<AppListResponse>>> responseData = milkywayClient.appList(paramWebPage);
        List<AppListResponse> appListResponseList = responseData.getData().getData();
        return ResponseData.createBySuccess(appListResponseList);
    }

    @ApiOperation(value = "应用列表New", notes = "应用列表New")
    @PostMapping("/listByUser")
    public ResponseData<List<ApplicationListByUserDTO>> appListByUser(AuthUser authUser){
        return ResponseData.createBySuccess(grApplicationService.getAppListByUser(authUser));
    }

}
