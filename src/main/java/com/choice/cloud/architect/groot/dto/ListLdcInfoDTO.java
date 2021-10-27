package com.choice.cloud.architect.groot.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * @author zhangkun
 */
@Data
@ToString(callSuper = true)
@ApiModel("LDC 详情列表Model")
@Builder
public class ListLdcInfoDTO {
    @ApiModelProperty(value = "LDC")
    private String ldc;
    @ApiModelProperty(value = "nodeName")
    private String nodeName;
    @ApiModelProperty(value = "cpu")
    private String cpu;
    @ApiModelProperty(value = "memory")
    private String memory;
    @ApiModelProperty(value = "pod")
    private String pod;
}
