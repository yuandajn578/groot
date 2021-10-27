package com.choice.cloud.architect.groot.controller;

import com.choice.cloud.architect.groot.dto.ListBuildRecordDTO;
import com.choice.cloud.architect.groot.option.WebPage;
import com.choice.cloud.architect.groot.request.ListBuildRecordRequest;
import com.choice.cloud.architect.groot.response.Page;
import com.choice.cloud.architect.groot.response.ResponseData;
import com.choice.cloud.architect.groot.service.inner.GrBuildRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.validation.Valid;
import java.util.List;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/6 11:18
 */
@Api(value = "构建", tags = "构建记录")
@RestController
@RequestMapping("/api/build")
@Slf4j
public class GrPublishBuildController {

    @Autowired
    private GrBuildRecordService grBuildRecordService;

    @ApiOperation(value = "构建记录分页查询", notes = "构建记录分页查询")
    @PostMapping("/record/list")
    public ResponseData<List<ListBuildRecordDTO>> publishPageList(@Valid @RequestBody ListBuildRecordRequest request) {
        WebPage<List<ListBuildRecordDTO>> list = grBuildRecordService.pageList(request);
        Page page = new Page();
        page.setPageNum(list.getPageNum());
        page.setPageSize(list.getPageSize());
        page.setTotal(list.getTotal().intValue());
        return ResponseData.createBySuccess(page, list.getData());
    }

    @ApiOperation(value = "构建结果回调", notes = "构建结果回调")
    @GetMapping("/record/callback")
    public ResponseData buildResultCallback(@RequestParam String jobName, @RequestParam int num) {

        log.info("jenkins构建结果回调jobName:{}  buildNum:{}", jobName, num);
        // 防止服务分组异步丢失
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        RequestContextHolder.setRequestAttributes(attributes, true);
        grBuildRecordService.updateRecordResult(jobName, num);
        return ResponseData.createBySuccess();
    }
}
