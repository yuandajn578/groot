package com.choice.cloud.architect.groot.controller;

import com.choice.cloud.architect.groot.dto.AppEnvRelDTO;
import com.choice.cloud.architect.groot.model.GrAppEnvRel;
import com.choice.cloud.architect.groot.response.ResponseData;
import com.choice.cloud.architect.groot.service.inner.GrAppEnvRelService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(description = "应用换将关联",tags = "应用换将关联")
@RestController
@RequestMapping("/api/appEnvRel")
@Slf4j
public class GrAppEnvRelController {

    @Autowired
    private GrAppEnvRelService grAppEnvRelService;

    @Deprecated
    @PostMapping("/list")
    public ResponseData<List<GrAppEnvRel>> listByAppCodeAndEnv(@RequestParam(value = "changeId",required = true) String changeId,
                                                               @RequestParam(value = "appCode",required = true)String appCode,
                                                               @RequestParam(value = "envType",required = false)String envType,
                                                               @RequestParam(value = "envCode",required = false)String envCode){
        List<GrAppEnvRel> grAppEnvRels = grAppEnvRelService.listByAppCodeAndEnv(changeId, appCode, envType, envCode);
        return ResponseData.createBySuccess(grAppEnvRels);
    }

    @GetMapping("/listByEnvType")
    public ResponseData<List<AppEnvRelDTO>> listByEnvType(@RequestParam(value = "envType") String envType){
        return ResponseData.createBySuccess(grAppEnvRelService.queryAppEnvRelByEnvType(envType));
    }

}
