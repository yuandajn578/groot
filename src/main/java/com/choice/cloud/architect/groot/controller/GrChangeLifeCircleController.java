package com.choice.cloud.architect.groot.controller;

import com.choice.cloud.architect.groot.dto.GrChangeLifeCirclePageDTO;
import com.choice.cloud.architect.groot.option.WebPage;
import com.choice.cloud.architect.groot.request.PageChangeLifeCircleRequest;
import com.choice.cloud.architect.groot.response.Page;
import com.choice.cloud.architect.groot.response.ResponseData;
import com.choice.cloud.architect.groot.service.inner.GrChangeLifeCircleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/14 10:14
 */
@Api(tags = "变更生命周期")
@RestController
@RequestMapping("/api/change/lifecircle")
public class GrChangeLifeCircleController {

    @Autowired
    private GrChangeLifeCircleService grChangeLifeCircleService;

    @ApiOperation(value = "变更生命周期分页查询", notes = "变更生命周期分页查询")
    @PostMapping("/page")
    public ResponseData<List<GrChangeLifeCirclePageDTO>> publishPageList(@RequestBody PageChangeLifeCircleRequest request) {
        WebPage<List<GrChangeLifeCirclePageDTO>> list = grChangeLifeCircleService.pageList(request);
        Page page = new Page();
        page.setPageNum(list.getPageNum());
        page.setPageSize(list.getPageSize());
        page.setTotal(list.getTotal().intValue());
        return ResponseData.createBySuccess(page, list.getData());
    }


    @ApiOperation(value = "变更生命周期环境类型map", notes = "变更生命周期环境类型map")
    @PostMapping("/envlist")
    public ResponseData<Map<String, List<GrChangeLifeCirclePageDTO>>> envTypeList(@RequestBody PageChangeLifeCircleRequest request) {
        Map<String, List<GrChangeLifeCirclePageDTO>> resMap = grChangeLifeCircleService.envTypeList(request);
        return ResponseData.createBySuccess(resMap);
    }
}
