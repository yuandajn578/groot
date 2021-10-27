package com.choice.cloud.architect.groot.controller;

import com.choice.cloud.architect.groot.dto.StatisticsBoxDTO;
import com.choice.cloud.architect.groot.response.ResponseData;
import com.choice.cloud.architect.groot.service.inner.DashboardService;
import com.netflix.discovery.converters.Auto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: zhangguoquan
 * @Date: 2020/4/16 14:19
 */
@Api(value = "dashboard", tags = "dashboard")
@RestController
@RequestMapping(value = "/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @ApiOperation(value = "统计数据盒子")
    @GetMapping(value = "/statistics/box")
    public ResponseData<StatisticsBoxDTO> statisticsBox() {
        return ResponseData.createBySuccess(dashboardService.statisticsBox());
    }
}
