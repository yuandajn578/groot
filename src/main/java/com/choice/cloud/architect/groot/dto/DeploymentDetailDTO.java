package com.choice.cloud.architect.groot.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author zhangkun
 */
@Data
@ToString
public class DeploymentDetailDTO {
    @ApiModelProperty(value = "名称")
    private String app;
    @ApiModelProperty(value = "状态")
    private String status;
    @ApiModelProperty(value = "副本数")
    private Integer replicas;
    @ApiModelProperty(value = "镜像")
    private String images;
}
