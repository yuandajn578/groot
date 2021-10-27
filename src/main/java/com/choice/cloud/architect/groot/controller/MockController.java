package com.choice.cloud.architect.groot.controller;

import com.alibaba.fastjson.JSONObject;
import com.choice.cloud.architect.groot.gitlab.GitlabService;
import com.choice.cloud.architect.groot.response.ResponseData;
import com.choice.cloud.architect.groot.service.outter.MockService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @classDesc:
 * @author:yuxiaopeng
 * @createTime: 2019-02-25
 * @version: v1.0.0
 * @email: yxpchoicesoft.com.cn
 */
@Api(description = "mock",tags = "mock")
@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/mock")
public class MockController {

    private MockService mockService;
    @Autowired
    private GitlabService gitlabService;

    @ApiOperation(value = "mock测试",notes = "mock测试")
    @PostMapping("/test")
    public ResponseData reportAlarm(){

        return mockService.test();
    }

    @ApiOperation(value = "mock测试",notes = "mock测试")
    @PostMapping("/git/merge")
    public ResponseData testKing(@RequestBody JSONObject requestBody) {
        String projectUrl = requestBody.getString("url");
        String sourceBranch = requestBody.getString("sourceBranch");
        String targetBranch = requestBody.getString("targetBranch");
        String title = requestBody.getString("title");
        gitlabService.merge(projectUrl, sourceBranch, targetBranch, title);
        return ResponseData.createBySuccess();
    }

    @ApiOperation(value = "mock测试接收任意参数",notes = "mock测试接收任意参数")
    @GetMapping("/anybody")
    public ResponseData anybody(@RequestParam("code") String code, @RequestParam("state") String state ) {
        log.info("mock测试接收code {}", code);
        log.info("mock测试接收state {}", state);
        return ResponseData.createBySuccess();
    }

}
