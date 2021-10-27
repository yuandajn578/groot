package com.choice.cloud.architect.groot.request.k8s;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Author: zhangguoquan
 * @Date: 2020/7/14 15:32
 */
@Data
public class PodLogRequest {

    @NotNull
    private String envType;

    @NotNull
    private String envCode;

    @NotNull
    private String podName;

    @NotNull
    private Integer periodSeconds;

    @NotNull
    private Integer lineLength;

    @NotNull
    private Integer tailLines;

    public String getNamespace() {
        return envType.concat("-").concat(envCode);
    }
}
