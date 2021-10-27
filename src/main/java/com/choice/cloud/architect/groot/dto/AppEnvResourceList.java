package com.choice.cloud.architect.groot.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author zhangkun
 */
@ApiModel(value = "应用资源管理列表")
@Data
@ToString
public class AppEnvResourceList {
    @ApiModelProperty(value = "容器IP")
    private String nodeIP;
}
