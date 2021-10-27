package com.choice.cloud.architect.groot.controller;

import com.choice.cloud.architect.groot.dto.ListIdcLdcDTO;
import com.choice.cloud.architect.groot.response.ResponseData;
import com.choice.cloud.architect.groot.service.inner.LdcPublishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zhangkun
 */
@Api(value = "LDC", tags = "LDC视角")
@RestController
@RequestMapping("/api/ldc")
@Slf4j
public class LdcController {
    @Autowired
    private LdcPublishService ldcPublishService;

    @ApiOperation(value = "ldc list", notes = "ldc列表")
    @GetMapping("/list")
    public ResponseData<List<ListIdcLdcDTO>> ldcList() {
        return ResponseData.createBySuccess(ldcPublishService.listLdcInfo());
    }
}
