package com.choice.cloud.architect.groot.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

/**
 * @author zhangkun
 */
@ApiModel(value = "查询某个应用在某个环境分组下的资源列表")
@Data
@ToString
public class ListAppEnvResourceRequest {
    @ApiModelProperty(value = "应用编码")
    @NotEmpty
    private String appCode;
    @ApiModelProperty(value = "环境类型")
    @NotEmpty
    private String envType;
    @ApiModelProperty(value = "环境编码")
    @NotEmpty
    private String envCode;
}
