package com.choice.cloud.architect.groot.service.k8s.impl;

import com.choice.cloud.architect.groot.option.WebPage;
import com.choice.cloud.architect.groot.request.k8s.PodQueryRequest;
import com.choice.cloud.architect.groot.response.k8s.PodResponse;
import com.choice.cloud.architect.groot.service.k8s.K8sService;
import com.google.common.collect.Lists;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.Watch;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.dsl.FilterWatchListDeletable;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author lanboo
 * @date 2020/6/2 14:28
 * @desc
 */
@Service
public class K8sServiceImpl implements K8sService {

    @Resource(name = "offlineKubernetesClient")
    private KubernetesClient offlineKubernetesClient;

    @Resource(name = "onlineKubernetesClient")
    private KubernetesClient onlineKubernetesClient;



    @Override
    public WebPage<List<PodResponse>> listAllPods(PodQueryRequest request) {
        List<PodResponse> podResponseList = Lists.newArrayList();

        FilterWatchListDeletable<Pod, PodList, Boolean, Watch, Watcher<Pod>> pods = null;
        if ("offline".equals(request.getEnv())) {
            pods = offlineKubernetesClient.pods();
        } else if ("online".equals(request.getEnv())) {
            pods = onlineKubernetesClient.pods();
        }
        if (null == pods || null == pods.list()) {
            return WebPage.empty();
        }
        List<Pod> items = pods.list().getItems();
        if (CollectionUtils.isEmpty(items)) {
            return WebPage.empty();
        }
        List<Pod> pageingList = WebPage.pageingList(items, request.getPageNum(), request.getPageSize());
        int total = items.size();
        if (CollectionUtils.isEmpty(pageingList)) {
            return WebPage.empty();
        }
        for (Pod pod : pageingList) {
            PodResponse response = new PodResponse();
            response.setKind(pod.getKind());
            ObjectMeta metadata = pod.getMetadata();
            if (null != metadata) {
                response.setName(metadata.getName());
                response.setNameSpace(metadata.getNamespace());
                response.setCreatTime(metadata.getCreationTimestamp());
            }
            podResponseList.add(response);
        }

        return new WebPage<>(request.getPageNum(), request.getPageSize(), total, podResponseList);
    }
}
