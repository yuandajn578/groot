package com.choice.cloud.architect.groot.service.inner;

import com.choice.cloud.architect.groot.dto.LdcPodInfoDTO;
import com.choice.cloud.architect.groot.dto.ListEcsInfoDTO;
import com.choice.cloud.architect.groot.dto.ListNamespaceDetailDTO;
import com.choice.cloud.architect.groot.dto.ListPodInfoDTO;
import com.choice.cloud.architect.groot.request.*;
import com.choice.cloud.architect.groot.request.k8s.PodLogRequest;
import com.choice.driver.jwt.entity.AuthUser;

import java.util.List;

/**
 * @author zhangkun
 */
public interface ContainerManageService {
    List<ListNamespaceDetailDTO> listNamespaceDetail(QueryNamespaceDetailRequest request, AuthUser authUser);

    void delete(KubernetesDeleteRequest request);

    Boolean deletePod(String namespaceName, String podName, AuthUser authUser);

    void redeploy(String namespaceName, String appCode, AuthUser authUser);

    void rollingDeploy(String namespaceName, String appCode, AuthUser authUser);

    List<ListPodInfoDTO> podList(QueryPodListRequest request);

    List<String> getNsNameList(AuthUser authUser);

    void rollbackToPreviousRevision(String envType, String envCode,  String appCode);

    void rollbackToPreviousRevision(String namespace, String deployment);

    void rollbackToSpecifiedRevision(String namespace, String deployment, String revision);

    String getDeploymentRevision(String envType, String envCode, String appCode);

    void updateReplicas(String namespaceName, String appCode, int replicas);

    List<LdcPodInfoDTO> listLdcPodInfo(QueryLdcPodListRequest request);

    void deleteStatefulSetPods(String envType, String envCode, String podName, AuthUser authUser);

    String getPodOwnerKind(String namespaceName, String podName);

    void createStatefulSetResource(String namespaceName, String existedName, String newName);

    List<ListEcsInfoDTO> listNodeInfo(ListNodeInfoRequest request);

    List<ListPodInfoDTO> batchQueryPodByName(BatchQueryPodRequest request);

    void removeDeployment(String namespaceName, String appCode);

    List<ListPodInfoDTO> statefulSetTypePodList(QueryPodListRequest request);

    List<ListPodInfoDTO> deploymentTypePodList(QueryPodListRequest request);

    String getLog(PodLogRequest request);
}
