package com.choice.cloud.architect.groot.service.inner.impl;

import com.choice.cloud.architect.groot.dao.GrAppEnvDefaultConfigMapper;
import com.choice.cloud.architect.groot.model.GrAppEnvDefaultConfig;
import com.choice.cloud.architect.groot.option.GlobalConst;
import com.choice.cloud.architect.groot.request.PublishBuildRequest;
import com.choice.cloud.architect.groot.service.inner.GrAppEnvDefaultConfigService;
import com.choice.driver.jwt.entity.AuthUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/9 14:24
 */
@Service
public class GrAppEnvDefaultConfigServiceImpl extends BaseService implements GrAppEnvDefaultConfigService {

    @Autowired
    private GrAppEnvDefaultConfigMapper appEnvDefaultConfigMapper;

    @Override
    public GrAppEnvDefaultConfig getAppEnvDefaultConfig(String appCode, String envType, String envCode) {

        return appEnvDefaultConfigMapper.getByAppAndEnv(appCode, envType, envCode, GlobalConst.NOT_DELETE);
    }
}
