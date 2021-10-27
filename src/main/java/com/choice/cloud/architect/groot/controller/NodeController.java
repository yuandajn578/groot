package com.choice.cloud.architect.groot.controller;

import com.choice.cloud.architect.groot.dto.ListIdcLdcDTO;
import com.choice.cloud.architect.groot.dto.ListLdcInfoDTO;
import com.choice.cloud.architect.groot.response.ResponseData;
import com.choice.cloud.architect.groot.response.code.SysResponseCode;
import com.choice.cloud.architect.groot.service.inner.IdcPublishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/3 17:14
 */
@Api(value = "Node", tags = "Node视角")
@RestController
@RequestMapping("/api/node")
@Slf4j
public class NodeController {

    @ApiOperation(value = "node list", notes = "node列表")
    @GetMapping("/list")
    public ResponseData<List<ListIdcLdcDTO>> nodeList() {
        return null;
//        return ResponseData.createBySuccess(idcPublishService.listLdcInfo());
    }
}
