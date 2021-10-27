package com.choice.cloud.architect.groot.service.inner.impl;

import com.alibaba.fastjson.JSON;
import com.choice.cloud.architect.groot.constants.EnvConstants;
import com.choice.cloud.architect.groot.constants.K8sConstants;
import com.choice.cloud.architect.groot.dto.*;
import com.choice.cloud.architect.groot.enums.EnvTypeEnum;
import com.choice.cloud.architect.groot.exception.ServiceException;
import com.choice.cloud.architect.groot.model.GrAppEnvRel;
import com.choice.cloud.architect.groot.remote.AppListResponse;
import com.choice.cloud.architect.groot.remote.ApplicationListByUserParam;
import com.choice.cloud.architect.groot.remote.enums.RoleEnum;
import com.choice.cloud.architect.groot.remote.milkyway.MilkywayClient;
import com.choice.cloud.architect.groot.request.*;
import com.choice.cloud.architect.groot.request.k8s.PodLogRequest;
import com.choice.cloud.architect.groot.response.ResponseData;
import com.choice.cloud.architect.groot.response.code.EnvResponseCode;
import com.choice.cloud.architect.groot.service.inner.ContainerManageService;
import com.choice.cloud.architect.groot.service.inner.GrAppEnvRelService;
import com.choice.cloud.architect.groot.service.inner.PodManagementPermissionService;
import com.choice.cloud.architect.groot.support.IdcLdcHolder;
import com.choice.cloud.architect.groot.util.CommonUtil;
import com.choice.cloud.architect.groot.util.NamespaceUtils;
import com.choice.driver.jwt.entity.AuthUser;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.*;
import io.fabric8.kubernetes.api.model.extensions.DeploymentRollback;
import io.fabric8.kubernetes.api.model.extensions.DeploymentRollbackBuilder;
import io.fabric8.kubernetes.api.model.extensions.RollbackConfig;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.Watch;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.dsl.FilterWatchListDeletable;
import io.fabric8.kubernetes.client.dsl.PodResource;
import io.fabric8.kubernetes.client.dsl.RollableScalableResource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * Cluster -> Namespace -> Deployment -> Pod
 * </p>
 *
 * @author zhangkun
 */
@Service
@Slf4j
public class ContainerManageServiceImpl extends BaseService implements ContainerManageService {
    @Resource(name = "offlineKubernetesClient")
    private KubernetesClient offlineKubernetesClient;
    @Resource(name = "onlineKubernetesClient")
    private KubernetesClient onlineKubernetesClient;

    @Autowired
    private PodManagementPermissionService podManagementPermissionService;

    @Autowired
    private MilkywayClient milkywayClient;

    @Autowired
    private GrAppEnvRelService grAppEnvRelService;

    @Override
    public List<ListNamespaceDetailDTO> listNamespaceDetail(QueryNamespaceDetailRequest request, AuthUser authUser) {
        // 1.通过人员查询权限应用
        List<AppListResponse> userAuthorizedApp = getUserAuthorizedApp(authUser);
        if (CollectionUtils.isEmpty(userAuthorizedApp)) {
            log.info("用户uid={},mobile={}查询无应用", authUser.getUid(), authUser.getMobile());
            return Collections.emptyList();
        }

        // 2.获取应用发布过的环境
        List<String> appCodeList = userAuthorizedApp.stream().map(AppListResponse::getAppCode).collect(Collectors.toList());
        List<String> nsNameList = getNsNameList(appCodeList);

        // 3.查询namespace下的deployment
        List<ListNamespaceDetailDTO> listNamespaceDetailDTOList = Lists.newArrayList();
        for (String nsName : nsNameList) {
            // 获取指定namespace下的deployment
            List<DeploymentDetailDTO> deploymentDetailDTOS = Lists.newArrayList();

            String[] filterLabelValues = filterLabelValues(nsName, appCodeList);

            KubernetesClient kubernetesClient = getKubernetesClientByNamespace(nsName);
            FilterWatchListDeletable<Deployment, DeploymentList, Boolean, Watch, Watcher<Deployment>> deployments =
                    kubernetesClient.apps().deployments().inNamespace(nsName)
                            .withLabelIn("workload.user.cattle.io/workloadselector", filterLabelValues);

            if (null == deployments) {
                continue;
            }

            DeploymentList deploymentList = deployments.list();

            List<Deployment> deploymentItemList = deploymentList.getItems();
            for (Deployment deployment : deploymentItemList) {
                DeploymentDetailDTO deploymentDetailDTO = new DeploymentDetailDTO();
                String appName = deployment.getMetadata().getName();

                if (!appCodeList.contains(appName)) {
                    continue;
                }

                // 根据最后更新时间排序
                if (CollectionUtils.isNotEmpty(deployment.getStatus().getConditions())) {
                    deployment.getStatus().getConditions().sort(Comparator.comparing(DeploymentCondition::getLastUpdateTime));
                    String status = deployment.getStatus().getConditions().get(0).getStatus();
                    if ("True".equals(status)) {
                        deploymentDetailDTO.setStatus("active");
                    } else {
                        deploymentDetailDTO.setStatus("warning");
                    }
                } else {
                    // Ignore
                }
                Integer replicas = deployment.getSpec().getReplicas();
                List<Container> containers = deployment.getSpec().getTemplate().getSpec().getContainers();
                List<String> imageNameList = Lists.newArrayList();
                if (CollectionUtils.isNotEmpty(containers)) {
                    for (Container container : containers) {
                        imageNameList.add(container.getImage());
                    }
                }
                deploymentDetailDTO.setApp(appName);
                deploymentDetailDTO.setReplicas(replicas);
                deploymentDetailDTO.setImages(Joiner.on(",").join(imageNameList.iterator()));

                deploymentDetailDTOS.add(deploymentDetailDTO);
            }

            ListNamespaceDetailDTO listNamespaceDetailDTO = new ListNamespaceDetailDTO();
            listNamespaceDetailDTO.setNamespace(nsName);
            listNamespaceDetailDTO.setDeploymentDetails(deploymentDetailDTOS);

            listNamespaceDetailDTOList.add(listNamespaceDetailDTO);
        }

        return listNamespaceDetailDTOList;
    }

    @Override
    public void updateReplicas(String namespaceName, String appCode, int replicas) {
        if (replicas > 2) {
            throw new ServiceException(EnvResponseCode.REPLICAS_MORE_THAN_TWO);
        }
        KubernetesClient client = namespaceName.startsWith("pro") ? onlineKubernetesClient : offlineKubernetesClient;
        client.apps().deployments().inNamespace(namespaceName).withName(appCode).scale(replicas, true);
    }

    @Override
    public List<LdcPodInfoDTO> listLdcPodInfo(QueryLdcPodListRequest request) {
        List<LdcPodInfoDTO> ldcPodInfoList = Lists.newArrayList();
        String namespaceName = NamespaceUtils.getNamespace(request.getEnvType(), request.getEnvCode());
        QueryPodListRequest queryPodListRequest = new QueryPodListRequest();
        queryPodListRequest.setNamespaceName(namespaceName);
        queryPodListRequest.setAppCode(request.getAppCode());

        List<ListPodInfoDTO> podListDTOList = this.podList(queryPodListRequest);

        if (podListDTOList.isEmpty()) {
            return ldcPodInfoList;
        }

        Map<String, List<ListPodInfoDTO>> ldc2PodMap = podListDTOList.stream()
                .filter(e -> !e.getIdc().equals("-"))
                .collect(Collectors.groupingBy(ListPodInfoDTO::idcWithLdc));

        ldc2PodMap.forEach((key, value) -> {
            LdcPodInfoDTO ldcPodInfoDTO = new LdcPodInfoDTO();
            String idc = Splitter.on("-").splitToList(key).get(0);
            String ldc = Splitter.on("-").splitToList(key).get(1);

            ldcPodInfoDTO.setId(CommonUtil.getUUIdUseMongo());
            ldcPodInfoDTO.setIdc(idc);
            ldcPodInfoDTO.setLdc(ldc);
            ldcPodInfoDTO.setPodNum(value.size());
            ldcPodInfoDTO.setPodList(value);

            ldcPodInfoList.add(ldcPodInfoDTO);
        });

        return ldcPodInfoList;
    }

    @Override
    public void deleteStatefulSetPods(String envType, String envCode, String podName, AuthUser authUser) {
        if (!podManagementPermissionService.hasPermission(authUser)) {
            throw new ServiceException("无权限操作，请联系运维人员处理");
        }

        String namespaceName = envType.concat("-").concat(envCode);
        String podOwnerKind = getPodOwnerKind(namespaceName, podName);

        if (null == podOwnerKind) {
            log.info("Namespace = {}, PodName={}的Pod查询不到", namespaceName, podName);
            return;
        }

        boolean delete = false;
        KubernetesClient kubernetesClient = getKubernetesClientByNamespace(namespaceName);

        if (K8sConstants.K8S_RESOURCE_OBJECT_STATEFUL_SET.equals(podOwnerKind)) {
            String workLoadName = podName.substring(0, podName.length() - 2);
            delete = kubernetesClient.apps().statefulSets().inNamespace(namespaceName).withName(workLoadName).delete();
        } else {
            log.info("podName = {} is not StatefulSet deploy", podName);
        }

        if (delete) {
            log.info("user = {}, namespace = {}, podName = {} delete success", currentUserName(authUser), namespaceName, podName);
        } else {
            log.info("user = {}, namespace = {}, podName = {} delete failed", currentUserName(authUser), namespaceName, podName);
        }
    }

    @Override
    public String getPodOwnerKind(String namespaceName, String podName) {
        Pod pod = getPod(namespaceName, podName);

        if (null != pod) {
            return pod.getMetadata().getOwnerReferences().get(0).getKind();
        }

        return null;
    }

    @Override
    public void createStatefulSetResource(String namespaceName, String existedName, String newName) {
        KubernetesClient kubernetesClient = getKubernetesClientByNamespace(namespaceName);

        StatefulSet statefulSet = kubernetesClient.apps().statefulSets().inNamespace(namespaceName).withName(existedName).get();
        StatefulSet newStatefulSet = new StatefulSet();
        BeanUtils.copyProperties(statefulSet, newStatefulSet);
        ObjectMeta metadata = statefulSet.getMetadata();
        metadata.setName(newName);
        metadata.setResourceVersion(null);
        newStatefulSet.setMetadata(metadata);
        kubernetesClient.apps().statefulSets().create(newStatefulSet);
    }

    @Override
    public List<ListEcsInfoDTO> listNodeInfo(ListNodeInfoRequest request) {
        List<ListEcsInfoDTO> nodeInfoList = Lists.newArrayList();
        DecimalFormat df = new DecimalFormat("0.00");

        KubernetesClient kubernetesClient = getKubernetesClientByIdc(request.getIdc());
        FilterWatchListDeletable<Node, NodeList, Boolean, Watch, Watcher<Node>> nodes
                = kubernetesClient.nodes();

        if (StringUtils.isNotBlank(request.getIdc())) {
            nodes.withLabelIn("nodeidc", request.getIdc());
        }

        if (StringUtils.isNotBlank(request.getLdc())) {
            nodes.withLabelIn("nodeldc", request.getLdc());
        }

        if (StringUtils.isNotBlank(request.getIp())) {
            nodes.withLabelIn("kubernetes.io/hostname", request.getIp());
        }

        List<Node> nodeList = nodes.list().getItems();

        nodeList.forEach(item -> {
            ListEcsInfoDTO listEcsInfoDTO = new ListEcsInfoDTO();

            String nodeIp = item.getMetadata().getLabels().get("kubernetes.io/hostname");
            listEcsInfoDTO.setName(nodeIp);

            String cpuAllocatable = item.getStatus().getAllocatable().get("cpu").getAmount();
            String cpuCapacity = item.getStatus().getCapacity().get("cpu").getAmount();
            String cpu = df.format(Double.parseDouble(cpuAllocatable)) + "/" + df.format(Double.valueOf(cpuCapacity));
            listEcsInfoDTO.setCpu(cpu);

            String memoryAllocatable = item.getStatus().getAllocatable().get("memory").getAmount();
            String memoryCapacity = item.getStatus().getCapacity().get("memory").getAmount();
            String memory = df.format(Double.valueOf(memoryAllocatable) / (1024 * 1024)) + "/" + df.format(Double.valueOf(memoryCapacity) / (1024 * 1024));
            listEcsInfoDTO.setMemory(memory);

            String podAllocatable = item.getStatus().getAllocatable().get("pods").getAmount();
            String podCapacity = item.getStatus().getCapacity().get("pods").getAmount();
            String pod = podAllocatable + "/" + podCapacity;
            listEcsInfoDTO.setPod(pod);

            List<NodeCondition> conditions = item.getStatus().getConditions();
            for (NodeCondition condition : conditions) {
                if (condition.getType().equals("Ready")) {
                    if (condition.getStatus().equals("True")) {
                        listEcsInfoDTO.setStatus("active");
                    } else {
                        listEcsInfoDTO.setStatus(condition.getReason());
                    }
                }
            }

            listEcsInfoDTO.setIdc(item.getMetadata().getLabels().get("nodeidc"));
            listEcsInfoDTO.setLdc(item.getMetadata().getLabels().get("nodeldc"));
            List<String> labels = item.getMetadata().getLabels()
                    .entrySet()
                    .stream()
                    .map(e -> e.getKey().concat(":").concat(e.getValue()))
                    .collect(Collectors.toList());

            listEcsInfoDTO.setLabels(labels);

            nodeInfoList.add(listEcsInfoDTO);
        });

        return nodeInfoList;
    }

    @Override
    public void delete(KubernetesDeleteRequest request) {
        Deployment deployment = new DeploymentBuilder()
                .withNewMetadata()
                .withName(request.getName())
                .endMetadata()
                .withNewSpec()
                .withReplicas(1)
                .withNewTemplate()
                .withNewMetadata()
                .addToLabels("app", request.getLableValue())
                .endMetadata()
                .withNewSpec()
                .addNewContainer()
                .withName(request.getName())
                .withImage(request.getImage())
                .addNewPort()
                .withContainerPort(request.getPort())
                .endPort()
                .endContainer()
                .endSpec()
                .endTemplate()
                .withNewSelector()
                .addToMatchLabels("app", request.getMatchLable())
                .endSelector()
                .endSpec()
                .build();
        //DELETE
//        offlineKubernetesClient.pods().inNamespace("ns1").withName("pod1").delete();
//        offlineKubernetesClient.resource(deployment).createOrReplace();
    }


    @Override
    public Boolean deletePod(String namespaceName, String podName, AuthUser authUser) {
        KubernetesClient kubernetesClient = namespaceName.startsWith("pro") ? onlineKubernetesClient : offlineKubernetesClient;
        boolean result = kubernetesClient.pods()
                .inNamespace(namespaceName).withName(podName).delete();
        if (!result) {
            log.warn("删除pod 404：namespace={}, podName={}", namespaceName, podName);
        } else {
            log.info("删除pod：namespace={}, podName={}, userId={}", namespaceName, podName, authUser.getUid());
        }
        return result;
    }

    @Override
    public void redeploy(String namespaceName, String appCode, AuthUser authUser) {
        QueryPodListRequest queryPodListRequest = new QueryPodListRequest();
        queryPodListRequest.setNamespaceName(namespaceName);
        queryPodListRequest.setAppCode(appCode);
        List<ListPodInfoDTO> pods = podList(queryPodListRequest);
        if (CollectionUtils.isEmpty(pods)) {
            throw new ServiceException(EnvResponseCode.CONTAINER_NOT_EXIST_POD);
        }

        KubernetesClient kubernetesClient = namespaceName.startsWith("pro") ? onlineKubernetesClient : offlineKubernetesClient;

        log.info("重启操作：namespace={}, appCode={}, operatorId={}", namespaceName, appCode, authUser.getUid());
        for (ListPodInfoDTO pod : pods) {
            boolean result = kubernetesClient.pods()
                    .inNamespace(namespaceName).withName(pod.getPodName()).delete();
            if (!result) {
                log.warn("删除pod 404：namespace={}, appCode={}, podName={}", namespaceName, appCode, pod.getPodName());
            }
        }
    }

    // 滚动更新部署
    @Override
    public void rollingDeploy(String namespaceName, String appCode, AuthUser authUser) {
        KubernetesClient kubernetesClient = namespaceName.startsWith("pro") ? onlineKubernetesClient : offlineKubernetesClient;
        kubernetesClient
                .apps()
                .deployments()
                .inNamespace(namespaceName)
                .withName(appCode)
                .edit()
                .editSpec()
                .editTemplate()
                .editMetadata()
                .addToAnnotations("redeploy_user_id", authUser.getUid() + "_" + System.currentTimeMillis())
                .endMetadata()
                .endTemplate()
                .endSpec()
                .done();
    }

    @Override
    public List<ListPodInfoDTO> podList(QueryPodListRequest request) {
        List<ListPodInfoDTO> podDTOList = Lists.newArrayList();

        podDTOList.addAll(statefulSetTypePodList(request));
        podDTOList.addAll(deploymentTypePodList(request));

        return podDTOList;
    }

    @Override
    public List<ListPodInfoDTO> batchQueryPodByName(BatchQueryPodRequest request) {
        String idc = request.getIdc();
        List<BatchQueryPodRequest.PodSimpleInfo> podSimpleInfoList = request.getPodInfoList();

        List<ListPodInfoDTO> podInfoList = Lists.newArrayList();
        KubernetesClient kubernetesClient = getKubernetesClientByIdc(idc);

        if (podSimpleInfoList.isEmpty()) {
            return podInfoList;
        }

        for (BatchQueryPodRequest.PodSimpleInfo podSimpleInfo : podSimpleInfoList) {
            PodResource<Pod, DoneablePod> podResource = kubernetesClient.pods().inNamespace(podSimpleInfo.getNamespace()).withName(podSimpleInfo.getPodName());
            ListPodInfoDTO listPodInfoDTO = transformPodToListPodInfoDTO(podResource.get());

            if (null == listPodInfoDTO) {
                continue;
            }

            podInfoList.add(listPodInfoDTO);
        }

        return podInfoList;
    }

    private ListPodInfoDTO transformPodToListPodInfoDTO(Pod pod) {
        if (null == pod) {
            return null;
        }

        ListPodInfoDTO listPodInfoDTO = new ListPodInfoDTO();

        listPodInfoDTO.setPodName(pod.getMetadata().getName());
        listPodInfoDTO.setPodIP(pod.getStatus().getPodIP());
        listPodInfoDTO.setNodeIP(pod.getStatus().getHostIP());
        List<ContainerStatus> containerStatuses = pod.getStatus().getContainerStatuses();
        if (CollectionUtils.isNotEmpty(containerStatuses)) {
            ContainerStatus containerStatus = containerStatuses.get(0);
            Boolean ready = containerStatus.getReady();
            listPodInfoDTO.setImage(containerStatus.getImage());
            if (ready) {
                listPodInfoDTO.setStatus("Ready");
            } else {
                listPodInfoDTO.setStatus("Unavailable");
            }
        }
        listPodInfoDTO.setNamespace(pod.getMetadata().getNamespace());
        Map<String, String> labels = pod.getMetadata().getLabels();
        String idc = labels.get("pod_idc");
        String ldc = labels.get("pod_ldc");
        listPodInfoDTO.setIdc(idc == null ? "-" : idc);
        listPodInfoDTO.setLdc(ldc == null ? "-" : ldc);
        LocalDateTime dateTime = LocalDateTime.parse(pod.getMetadata().getCreationTimestamp(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
        // 临时处理UTC通用标准时 加8个小时
        listPodInfoDTO.setCreationTimestamp(dateTime.plusHours(8L).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        return listPodInfoDTO;
    }

    @Override
    public List<String> getNsNameList(AuthUser authUser) {
        // 1.通过人员查询权限应用
        List<AppListResponse> userAuthorizedApp = getUserAuthorizedApp(authUser);
        if (CollectionUtils.isEmpty(userAuthorizedApp)) {
            log.info("用户uid={},mobile={}查询无应用", authUser.getUid(), authUser.getMobile());
            return Collections.emptyList();
        }

        List<String> appCodeList = userAuthorizedApp.stream().map(AppListResponse::getAppCode).collect(Collectors.toList());
        return getNsNameList(appCodeList);
    }

    @Override
    public void rollbackToPreviousRevision(String envType, String envCode, String appCode) {
        String namespace = envType.concat("-").concat(envCode);

        rollbackToPreviousRevision(namespace, appCode);
    }

    @Override
    public void rollbackToPreviousRevision(String namespace, String deployment) {
        RollableScalableResource<Deployment, DoneableDeployment> deployments = null;

        if (namespace.startsWith(EnvTypeEnum.test.value())) {
            deployments = offlineKubernetesClient.apps().deployments().inNamespace(namespace).withName(deployment);
        } else if (namespace.startsWith(EnvTypeEnum.pro.value())) {
            deployments = onlineKubernetesClient.apps().deployments().inNamespace(namespace).withName(deployment);
        }

        if (null == deployment) {
            throw new ServiceException(EnvResponseCode.SERVICE_GROUP_NOT_EXIST_DEPLOYMENT);
        }

        if (null == deployments.get()) {
            throw new ServiceException(EnvResponseCode.SERVICE_GROUP_NOT_EXIST_DEPLOYMENT);
        }

        RollbackConfig rollbackConfig = new RollbackConfig();

        rollbackConfig.setRevision(0L);

        DeploymentRollback deploymentRollback = new DeploymentRollback();
        deploymentRollback.setRollbackTo(rollbackConfig);
        deploymentRollback.setName(deployment);

        Status rollback = null;
        if (namespace.startsWith(EnvTypeEnum.test.value())) {
            rollback = offlineKubernetesClient.apps().deployments().inNamespace(namespace).withName(deployment).rollback(deploymentRollback);
        } else if (namespace.startsWith(EnvTypeEnum.pro.value())) {
            rollback = onlineKubernetesClient.apps().deployments().inNamespace(namespace).withName(deployment).rollback(deploymentRollback);
        }

        log.info("namespace = {}, deployment = {} 回滚到上一版本，Status = {}", namespace, deployment, JSON.toJSONString(rollback));
        if (!"Success".equals(rollback.getStatus())) {
            throw new ServiceException(EnvResponseCode.ROLLBACK_FAILURE);
        }
    }

    @Override
    public void rollbackToSpecifiedRevision(String namespace, String deployment, String revision) {
        KubernetesClient client = getKubernetesClientByNamespace(namespace);

        RollableScalableResource<Deployment, DoneableDeployment> deployments = client
                .apps()
                .deployments()
                .inNamespace(namespace)
                .withName(deployment);

        if (StringUtils.isBlank(deployment) || null == deployments.get()) {
            throw new ServiceException(EnvResponseCode.SERVICE_GROUP_NOT_EXIST_DEPLOYMENT);
        }

        DeploymentRollback deploymentRollback = new DeploymentRollbackBuilder()
                .withName(deployment)
                .withNewRollbackTo()
                .withRevision(Long.parseLong(revision))
                .endRollbackTo()
                .build();

        Status responseStatus = client
                .apps()
                .deployments()
                .inNamespace(namespace)
                .withName(deployment)
                .rollback(deploymentRollback);

        log.info("namespace = {}, deployment = {} 回滚指定版本版本，Revision = {}， Status = {}", namespace, deployment, revision, JSON.toJSONString(responseStatus));
        if (!"Success".equals(responseStatus.getStatus())) {
            throw new ServiceException(EnvResponseCode.ROLLBACK_FAILURE);
        }
    }

    @Override
    public String getDeploymentRevision(String envType, String envCode, String appCode) {
        String namespaceName = envType.concat("-").concat(envCode);
        KubernetesClient client = getKubernetesClientByNamespace(namespaceName);
        Map<String, String> map = client.apps().deployments().inNamespace(namespaceName)
                .withName(appCode).get().getMetadata().getAnnotations();

        return StringUtils.isNotBlank(map.get("deployment.kubernetes.io/revision"))
                ? map.get("deployment.kubernetes.io/revision") : "0";
    }

    /**
     * 移除namespace下的应用
     * @param namespaceName
     * @param appCode
     */
    @Override
    public void removeDeployment(String namespaceName, String appCode) {
        KubernetesClient kubernetesClient = namespaceName.startsWith("pro")? onlineKubernetesClient: offlineKubernetesClient;
        log.info("删除自建环境应用 namespace={}, deployment={}", namespaceName, appCode);
        kubernetesClient.apps().deployments().inNamespace(namespaceName).withName(appCode).delete();
    }

    @Override
    public List<ListPodInfoDTO> statefulSetTypePodList(QueryPodListRequest request) {
        KubernetesClient kubernetesClient = getKubernetesClientByNamespace(request.getNamespaceName());

        FilterWatchListDeletable<Pod, PodList, Boolean, Watch, Watcher<Pod>> pods;
        if (StringUtils.isBlank(request.getAppCode())) {
            pods = kubernetesClient.pods().inNamespace(request.getNamespaceName());
        } else {
            pods = kubernetesClient.pods().inNamespace(request.getNamespaceName())
                    .withLabelIn(K8sConstants.K8S_LABEL_POD_APPNAME, request.getAppCode());
        }

        return transformPodsToDTOList(pods);
    }

    @Override
    public List<ListPodInfoDTO> deploymentTypePodList(QueryPodListRequest request) {
        KubernetesClient kubernetesClient = getKubernetesClientByNamespace(request.getNamespaceName());

        FilterWatchListDeletable<Pod, PodList, Boolean, Watch, Watcher<Pod>> pods;
        if (StringUtils.isBlank(request.getAppCode())) {
            pods = kubernetesClient.pods().inNamespace(request.getNamespaceName());
        } else {
            pods = kubernetesClient.pods().inNamespace(request.getNamespaceName())
                    .withLabelIn("workload.user.cattle.io/workloadselector", filterLabelValue(request.getNamespaceName(), request.getAppCode()));
        }

        return transformPodsToDTOList(pods);
    }

    private List<ListPodInfoDTO> transformPodsToDTOList(FilterWatchListDeletable<Pod, PodList, Boolean, Watch, Watcher<Pod>> pods) {
        List<ListPodInfoDTO> podDTOList = Lists.newArrayList();

        if (null == pods) {
            return podDTOList;
        }

        PodList podList = pods.list();
        if (CollectionUtils.isEmpty(podList.getItems())) {
            return Collections.emptyList();
        }

        for (Pod pod : podList.getItems()) {
            ListPodInfoDTO listPodInfoDTO = transformPodToListPodInfoDTO(pod);

            podDTOList.add(listPodInfoDTO);
        }

        return podDTOList;
    }

    private List<String> getNsNameList(List<String> appCodeList) {
        // 2.获取应用发布过的环境
        List<GrAppEnvRel> grAppEnvRels = grAppEnvRelService.listByAppCode(appCodeList);
        if (CollectionUtils.isEmpty(grAppEnvRels)) {
            log.info("应用codeList={}查询发布记录", appCodeList);
            return Collections.emptyList();
        }
        List<String> nsNameList = grAppEnvRels.stream().map(GrAppEnvRel::getK8sNs).collect(Collectors.toList());
        ;

        return nsNameList;
    }

    private String filterLabelValue(String namespace, String appCode) {
        return "deployment-".concat(namespace).concat("-").concat(appCode);
    }

    private String[] filterLabelValues(String namespace, List<String> appCodeList) {
        String[] filterLabelValues = new String[appCodeList.size()];

        for (int i = 0; i < appCodeList.size(); i++) {
            String labelValue = "deployment-".concat(namespace).concat("-").concat(appCodeList.get(i));
            filterLabelValues[i] = labelValue;
        }

        return filterLabelValues;
    }

    private List<AppListResponse> getUserAuthorizedApp(AuthUser authUser) {
        ApplicationListByUserParam applicationListByUserParam = new ApplicationListByUserParam();
        applicationListByUserParam.setUserId(authUser.getUid());
        List<RoleEnum> roleEnumList = Lists.newArrayList(RoleEnum.publish);
        applicationListByUserParam.setRoleEnumList(roleEnumList);

        log.info("根据人员查询应用，入参 = {}", applicationListByUserParam);
        ResponseData<List<AppListResponse>> listResponseData = milkywayClient.listAppByUser(applicationListByUserParam);
        return listResponseData.getData();
    }

    private Pod getPod(String namespaceName, String podName) {
        KubernetesClient kubernetesClient = getKubernetesClientByNamespace(namespaceName);
        PodResource<Pod, DoneablePod> podResource = kubernetesClient.pods().inNamespace(namespaceName).withName(podName);

        Pod pod = Optional.of(podResource.get()).orElse(null);

        if (null == pod) {
            log.info("namespaceName = {}, podName = {} 查询pod信息为空", namespaceName, podName);
        }

        return pod;
    }

    private KubernetesClient getKubernetesClientByIdc(String idc) {
        List<String> onlineIdc = IdcLdcHolder.getOnlineIdc();
        List<String> offlineIdc = IdcLdcHolder.getOfflineIdc();

        String env = "";
        if (offlineIdc.contains(idc)) {
            env = EnvConstants.ENV_OFFLINE;
        }

        if (onlineIdc.contains(idc)) {
            env = EnvConstants.ENV_ONLINE;
        }

        log.info("getKubernetesClientByIdc, onlineIdc = {}, offlineIdc = {}, idc = {}, env = {}",onlineIdc, offlineIdc, idc, env);

        KubernetesClient kubernetesClient;

        if (EnvConstants.ENV_OFFLINE.equals(env)) {
            kubernetesClient = offlineKubernetesClient;
        } else if (EnvConstants.ENV_ONLINE.equals(env)) {
            kubernetesClient = onlineKubernetesClient;
        } else {
            throw new ServiceException("非法的环境类型");
        }

        return kubernetesClient;
    }

    private KubernetesClient getKubernetesClientByNamespace(String namespace) {
        KubernetesClient kubernetesClient;

        if (namespace.startsWith(EnvTypeEnum.test.value())) {
            kubernetesClient = offlineKubernetesClient;
        } else if (namespace.startsWith(EnvTypeEnum.pro.value())) {
            kubernetesClient = onlineKubernetesClient;
        } else {
            throw new ServiceException("非法的服务分组");
        }

        return kubernetesClient;
    }

    @Override
    public String getLog(PodLogRequest request) {
        String namespaceName = request.getNamespace();
        KubernetesClient client = getKubernetesClientByNamespace(namespaceName);
        String sourceLog = client
                .pods()
                .inNamespace(namespaceName)
                .withName(request.getPodName())
                .usingTimestamps()
                .sinceSeconds(request.getPeriodSeconds())
                .tailingLines(request.getTailLines())
                .withPrettyOutput()
                .getLog();

//        StringBuilder log = new StringBuilder();
//        Integer lineLength = request.getLineLength();

//        if (StringUtils.isNotBlank(sourceLog)) {
//            // 后端换行
//            boolean hasLn = false;
//            int i = 0;
//            while (i < sourceLog.length()) {
//                int endIndex = i + lineLength;
//                String s = sourceLog.substring(i, Math.min(endIndex, sourceLog.length()));
//                hasLn = s.contains("\n");
//                if (hasLn) {
//                    int indexLn = s.indexOf("\n");
//                    s = sourceLog.substring(i, i + indexLn + 1);
//                    log.append(s).append("\n");
//                    i = i + indexLn + 1;
//                } else {
//                    log.append(s).append("\n");
//                    i += lineLength;
//                }
//            }
//        }
        return sourceLog;
    }
}
