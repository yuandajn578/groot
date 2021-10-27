package com.choice.cloud.architect.groot.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author zhangkun
 */
@ApiModel(value = "应用管理列表")
@Data
@ToString
public class ListNamespaceDetailDTO {
    @ApiModelProperty("环境分组")
    private String namespace;
    @ApiModelProperty("应用部署详情")
    private List<DeploymentDetailDTO> deploymentDetails;
}
