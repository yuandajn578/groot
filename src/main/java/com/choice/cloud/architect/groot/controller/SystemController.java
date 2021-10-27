package com.choice.cloud.architect.groot.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @classDesc:
 * @author: yuxiaopeng
 * @createTime: 2020-02-22
 * @version: v1.0.0
 * @email: yuxiaopeng@choicesoft.com.cn
 */
@Api(description = "健康检查",tags = "健康检查")
@Slf4j
@RestController
public class SystemController {

    @ApiOperation(value = "健康检查",notes = "健康检查")
    @GetMapping("/ping")
    public String pong(){
        return "pong";
    }
}
