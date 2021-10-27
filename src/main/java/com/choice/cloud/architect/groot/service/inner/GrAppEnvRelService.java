package com.choice.cloud.architect.groot.service.inner;

import com.choice.cloud.architect.groot.dto.AppEnvRelDTO;
import com.choice.cloud.architect.groot.dto.ChangeBindEnvDTO;
import com.choice.cloud.architect.groot.model.GrAppEnvRel;
import com.choice.cloud.architect.groot.request.AddChangeBindEnvRequest;
import com.choice.cloud.architect.groot.request.RemoveChangeBindEnvRequest;
import com.choice.driver.jwt.entity.AuthUser;

import java.util.List;

/**
 * @author zhangkun
 */
public interface GrAppEnvRelService {
    void saveAppAndEnvRel(String changeId, String appCode, String envType, String envCode, AuthUser authUser);

    List<GrAppEnvRel> listByAppCode(List<String> appCodeList);

    List<GrAppEnvRel> listByEnv(String envType, String envCode);

    void initDefaultValue(String appCode);

    List<GrAppEnvRel> listByAppCodeAndEnv(String changeId,String appCode,String envType, String envCode);

    List<ChangeBindEnvDTO> getChangeBindEnv(String changeId);

    void addChangeBindEnv(AddChangeBindEnvRequest request, AuthUser authUser);

    void removeChangeBindEnv(RemoveChangeBindEnvRequest request);

    void delAppChangeEnvRel(String envType, String envCode);

    List<AppEnvRelDTO> queryAppEnvRelByEnvType(String envType);
}
