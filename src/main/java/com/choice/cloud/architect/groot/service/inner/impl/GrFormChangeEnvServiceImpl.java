package com.choice.cloud.architect.groot.service.inner.impl;

import com.choice.cloud.architect.groot.dao.GrFormChangeEnvMapper;
import com.choice.cloud.architect.groot.model.GrFormChangeEnv;
import com.choice.cloud.architect.groot.service.inner.GrFormChangeEnvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zhangkun
 */
@Service
public class GrFormChangeEnvServiceImpl implements GrFormChangeEnvService {
    @Autowired
    private GrFormChangeEnvMapper grFormChangeEnvMapper;

    @Override
    public void save(GrFormChangeEnv grFormChangeEnv) {
        grFormChangeEnvMapper.insert(grFormChangeEnv);
    }

    @Override
    public void deleteEnv(String envId) {
        grFormChangeEnvMapper.deleteEnv(envId);
    }
}
