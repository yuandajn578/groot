package com.choice.cloud.architect.groot.service.k8s;

import com.choice.cloud.architect.groot.option.WebPage;
import com.choice.cloud.architect.groot.request.QueryPodListRequest;
import com.choice.cloud.architect.groot.request.k8s.PodQueryRequest;
import com.choice.cloud.architect.groot.response.k8s.PodResponse;

import java.util.List;

/**
 * @author lanboo
 * @date 2020/6/2 14:05
 * @desc
 */
public interface K8sService {

    WebPage<List<PodResponse>> listAllPods(PodQueryRequest request);
}
