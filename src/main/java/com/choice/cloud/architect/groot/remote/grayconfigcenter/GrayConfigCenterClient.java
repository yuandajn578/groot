package com.choice.cloud.architect.groot.remote.grayconfigcenter;

import com.choice.cloud.architect.groot.remote.DefaultClientFallbackFactory;
import com.choice.cloud.architect.groot.response.ResponseData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author zhangkun
 */
@FeignClient(value = "gray-config-center",fallbackFactory = DefaultClientFallbackFactory.class)
public interface GrayConfigCenterClient {
    @PostMapping(value = "/open/api/env/gray/config")
    ResponseData<List<GrayRuleGroup>> getGrayRuleWithEnv(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String token, @RequestParam("envType") String envType, @RequestParam("envCode") String envCode);
}
