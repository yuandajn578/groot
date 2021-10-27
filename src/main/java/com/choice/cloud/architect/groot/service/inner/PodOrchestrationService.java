package com.choice.cloud.architect.groot.service.inner;

import com.choice.cloud.architect.groot.dto.OrchestratePodWhenScaleUpDTO;

import java.util.List;

/**
 * @author zhangkun
 */
public interface PodOrchestrationService {
    /**
     * pod 编排
     *
     * @param envType        环境类型
     * @param envCode        环境编码
     * @param appName        应用名
     * @param isFirstPublish 是否首次发布
     * @param podNameList    podName信息，用于非首次发布拼装pod发布编排json信息
     * @return pod编排信息
     */
    String orchestratePod(String envType, String envCode, String appName, String isFirstPublish, List<String> podNameList);

    /**
     * 扩容时进行pod编排
     *
     * @param orchestratePodWhenScaleUpDTO pod扩容参数
     * @return pod编排信息
     */
    String orchestratePodWhenScaleUp(OrchestratePodWhenScaleUpDTO orchestratePodWhenScaleUpDTO);
}
