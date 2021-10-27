package com.choice.cloud.architect.groot.request;

import lombok.Data;

/**
 * @Author: zhangguoquan
 * @Date: 2020/5/21 15:28
 */
@Data
public class DeletePodRequest {

    private String envType;

    private String envCode;

    private String podName;

    public String getNamespace() {
        return envType + "-" + envCode;
    }
}
