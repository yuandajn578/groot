package com.choice.cloud.architect.groot.controller;

import com.choice.cloud.architect.groot.model.GrPublishBlackRecord;
import com.choice.cloud.architect.groot.request.AddDateBlackRecordRequest;
import com.choice.cloud.architect.groot.request.AddWeekBlackRecordRequest;
import com.choice.cloud.architect.groot.response.ResponseData;
import com.choice.cloud.architect.groot.service.inner.PublishBlackListService;
import com.choice.driver.jwt.entity.AuthUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/25 15:10
 */
@Api(value = "发布日期黑名单", tags = "发布日期黑名单")
@RestController
@RequestMapping("/api/blacklist")
@Slf4j
public class PublishBlackListController {

    @Autowired
    private PublishBlackListService publishBlackListService;

    @ApiOperation(value = "黑名单记录查询", notes = "黑名单记录查询")
    @GetMapping("/listByType")
    public ResponseData<List<GrPublishBlackRecord>> getBlackList(@RequestParam String blackType){
        return ResponseData.createBySuccess(publishBlackListService.getBlackList(blackType));
    }

    @ApiOperation(value = "添加星期黑名单", notes = "添加星期黑名单")
    @PostMapping("addWeek")
    public ResponseData<String> addWeekBlackList(@RequestBody AddWeekBlackRecordRequest request, AuthUser authUser) {
        publishBlackListService.addWeekBlackRecord(request, authUser);
        return ResponseData.createBySuccess();
    }

    @ApiOperation(value = "添加日期黑名单", notes = "添加日期黑名单")
    @PostMapping("addDate")
    public ResponseData<String> addDateBlackList(@RequestBody AddDateBlackRecordRequest request, AuthUser authUser) {
        publishBlackListService.addDateBlackRecord(request, authUser);
        return ResponseData.createBySuccess();
    }
}
