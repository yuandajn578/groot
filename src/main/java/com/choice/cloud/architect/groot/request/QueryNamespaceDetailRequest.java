package com.choice.cloud.architect.groot.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author zhangkun
 */
@Data
@ToString
@ApiModel(value = "莹莹应用列表查询入参Model")
public class QueryNamespaceDetailRequest {
    @ApiModelProperty(value = "环境分组")
    private String namespace;
    @ApiModelProperty(value = "应用名称")
    private String app;
}
