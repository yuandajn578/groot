package com.choice.cloud.architect.groot.service.inner;

import com.choice.cloud.architect.groot.dto.*;
import com.choice.cloud.architect.groot.option.WebPage;
import com.choice.cloud.architect.groot.request.*;
import com.choice.driver.jwt.entity.AuthUser;

import java.util.List;
import java.util.Map;

/**
 * @author zhangkun
 */
public interface GrEnvService {

    void createEnv(CreateEnvRequest envCreateRequest, AuthUser authUser);

    WebPage<List<ListEnvDTO>> list(ListEnvRequest listEnvRequest);

    boolean existsEnv(String id);

    boolean checkIfEnvCodeExisted(String envType, String envCode);

    void delEnv(String id, AuthUser authUser);

    void changeStatus(ChangeEnvStatusRequest changeEnvStatusRequest, AuthUser authUser);

    List<SelectListEnvDTO> selectList(ListEnvRequest request);

    Map<String, List<ListAppEnvDTO>> listEnvByApp(ListEnvByAppRequest request);

    AppEnvDetailDTO getDetailByAppAndEnv(String oid, String appCode);

    void editEnv(EditEnvRequest request, AuthUser authUser);

    List<String> getEnvBindApp(QueryEnvBindAppRequest request);

    void bindChangeEnv(String changeId, String appCode, String envId, AuthUser authUser);

    GrAppEnvDefaultConfigDTO getAppPublishConfig(QueryAppPublishConfigRequest request);

    List<ChangeBindEnvDTO> getChangeBindEnv(String changeId);

    void addChangeBindEnv(AddChangeBindEnvRequest request, AuthUser authUser);

    WebPage<List<ListChangeDTO>> getEnvBindChangeList(QueryEnvBindChangeRequest request, AuthUser authUser);

    void removeChangeBindEnv(RemoveChangeBindEnvRequest request);

    Map<String, List<SelectListEnvDTO>> selectListEnvGroup(ListEnvRequest request);
}
