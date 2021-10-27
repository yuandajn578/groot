package com.choice.cloud.architect.groot.controller.k8s;

import com.choice.cloud.architect.groot.option.WebPage;
import com.choice.cloud.architect.groot.request.k8s.PodQueryRequest;
import com.choice.cloud.architect.groot.response.k8s.PodResponse;
import com.choice.cloud.architect.groot.service.k8s.K8sService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author lanboo
 * @date 2020/6/2 13:51
 * @desc
 */
@RestController
@RequestMapping("api/k8s")
public class K8sController {

    @Autowired
    private K8sService k8sService;

    @PostMapping("listAllPods")
    public WebPage<List<PodResponse>> listAllPods(@RequestBody @Validated PodQueryRequest request) {
        return k8sService.listAllPods(request);
    }
}
