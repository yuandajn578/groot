package com.choice.cloud.architect.groot.controller;

import com.choice.cloud.architect.groot.dto.ListIdcInfoDTO;
import com.choice.cloud.architect.groot.response.ResponseData;
import com.choice.cloud.architect.groot.service.inner.IdcPublishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/3 17:14
 */
@Api(value = "IDC", tags = "IDC视角")
@RestController
@RequestMapping("/api/idc")
@Slf4j
public class IdcController {
    @Autowired
    private IdcPublishService idcPublishService;

    @ApiOperation(value = "idc list", notes = "idc列表")
    @GetMapping("/list")
    public ResponseData<Set<ListIdcInfoDTO>> idcList() {
        return ResponseData.createBySuccess(idcPublishService.listIdcInfo());
    }
}
