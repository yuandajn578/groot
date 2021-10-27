package com.choice.cloud.architect.groot.dto;

import lombok.Data;

/**
 * @author zhangkun
 */
@Data
public class OrchestratePodWhenScaleUpDTO {
    private String envType;
    private String envCode;
    private String appCode;
    private String idc;
    private String ldc;
    private Integer scaleUpNum;
}
