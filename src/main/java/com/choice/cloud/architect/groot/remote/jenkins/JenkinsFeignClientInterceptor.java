package com.choice.cloud.architect.groot.remote.jenkins;

import com.choice.cloud.architect.groot.util.AppUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.auth.BasicAuthRequestInterceptor;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: zhangguoquan
 * @Date: 2020/4/8 14:11
 */
@Slf4j
public class JenkinsFeignClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        JenkinsClientBuilder builder = AppUtil.getBean(JenkinsClientBuilder.class);
        String username = builder.getUsername();
        String password = builder.getPassword();
        log.debug("JenkinsFeignClientInterceptor");
        new BasicAuthRequestInterceptor(username, password).apply(requestTemplate);
    }

}
