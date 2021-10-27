package com.choice.cloud.architect.groot.service.inner;

import com.choice.cloud.architect.groot.dto.*;
import com.choice.cloud.architect.groot.model.GrPublish;
import com.choice.cloud.architect.groot.option.WebPage;
import com.choice.cloud.architect.groot.remote.milkyway.AppConfigInfoResponseDTO;
import com.choice.cloud.architect.groot.request.*;
import com.choice.driver.jwt.entity.AuthUser;

import java.util.List;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/3 16:42
 */
public interface GrPublishService {

    WebPage<List<ListPublishDTO>> pageList(ListPublishRequest request);

    int update(UpdatePublishRequest request, AuthUser authUser);

    /**
     * 更改测试状态 并合并分支
     * @param param
     * @param updateUserName
     * @return
     */
    void updateTestStatusAndMergeMaster(UpdatePublishTestStatusRequest param, String updateUserName);

    int create(CreatePublishRequest request);

    PublishInfoDTO getByOid(String oid);

    void close(String oid, AuthUser authUser);

    BuildResultDTO build(PublishBuildRequest request, AuthUser authUser,
        boolean envConfig, boolean appConfig, AppConfigInfoResponseDTO infoByAppCode, PublishRequest publishRequest);

    BuildResultDTO execute(PublishRequest publishRequest, AuthUser authUser);

    BuildInfoDTO getBuildInfo(String appCode, String envType, String envCode);

    BuildInfoDTO getBuildInfo(String oid);

    BuildInfoDTO getBuildInfo(GrPublish grPublish);

    String stopBuildByNumber(String jobName, int buildNum);

    String getBuildNodeLog(String jobName, int buildNum, int nodeId);

    BuildInfoDTO getLastBuildInfo(String oid);

    void rollBackToPreviousRevision(DeploymentRollbackRequest request, AuthUser authUser);

    void rollbackToSpecifiedRevision(DeploymentRollbackRequest request, AuthUser authUser);

    String checkFirstPublish(String appCode, String envType, String envCode);

    List<ApplicationDeployInfoDTO> getApplicationDeployInfo(String appCode, String envType, String envCode);

    void scaleUpPod(ScaleUpPodRequest request, AuthUser authUser);
}
