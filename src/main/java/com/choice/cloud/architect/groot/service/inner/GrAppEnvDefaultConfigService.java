package com.choice.cloud.architect.groot.service.inner;

import com.choice.cloud.architect.groot.model.GrAppEnvDefaultConfig;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/9 14:21
 */
public interface GrAppEnvDefaultConfigService {

    GrAppEnvDefaultConfig getAppEnvDefaultConfig(String appCode, String envType, String envCode);
}
