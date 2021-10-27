package com.choice.cloud.architect.groot.request;

import lombok.Data;

import java.util.List;

/**
 * @author zhangkun
 */
@Data
public class BatchQueryPodRequest {
    private String idc;
    private List<PodSimpleInfo> podInfoList;

    @Data
    public static class PodSimpleInfo {
        private String namespace;
        private String podName;
    }
}
