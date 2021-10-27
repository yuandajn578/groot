package com.choice.cloud.architect.groot.service.inner;

import com.choice.cloud.architect.groot.model.GrFormChangeEnv;

/**
 * @author zhangkun
 */
public interface GrFormChangeEnvService {
    void save(GrFormChangeEnv grFormChangeEnv);
    void deleteEnv(String envId);
}
