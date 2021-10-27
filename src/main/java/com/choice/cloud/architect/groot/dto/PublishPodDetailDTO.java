package com.choice.cloud.architect.groot.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author zhangkun
 */
@Data
public class PublishPodDetailDTO {
    private String idc;
    private String ldc;
    @JSONField(name = "pod_name")
    private String podName;
}
