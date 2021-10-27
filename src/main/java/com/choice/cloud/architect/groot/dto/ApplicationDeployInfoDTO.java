package com.choice.cloud.architect.groot.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author zhangkun
 */
@Data
@ToString
@ApiModel(value = "Pod列表Model")
public class ApplicationDeployInfoDTO {
    @ApiModelProperty(value = "IDC")
    private String idc;
    @ApiModelProperty(value = "LDC")
    private String ldc;
    @ApiModelProperty(value = "podName")
    private String podName;
    @ApiModelProperty(value = "podIP")
    private String podIP;
    @ApiModelProperty(value = "nodeIP")
    private String nodeIP;
    @ApiModelProperty(value = "status")
    private String status;
    @ApiModelProperty(value = "image")
    private String image;
    @ApiModelProperty(value = "creationTimestamp")
    private String creationTimestamp;
}
