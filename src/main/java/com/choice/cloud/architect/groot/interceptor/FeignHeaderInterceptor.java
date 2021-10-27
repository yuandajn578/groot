package com.choice.cloud.architect.groot.interceptor;

import com.choice.cloud.architect.groot.util.CommonUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhangkun
 */
@Slf4j
public class FeignHeaderInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        HttpServletRequest currentRequest = CommonUtil.getCurrentRequest();
        if (null == currentRequest) {
            return;
        }
        log.debug("FeignHeaderInterceptor-Jwt");
        String jwtHeader = currentRequest.getHeader(HttpHeaders.AUTHORIZATION);
        log.info("jwtHeader = {}", jwtHeader);
        requestTemplate.header(HttpHeaders.AUTHORIZATION, jwtHeader);
    }
}
