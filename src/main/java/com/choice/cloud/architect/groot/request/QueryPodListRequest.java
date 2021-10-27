package com.choice.cloud.architect.groot.request;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

/**
 * @author zhangkun
 */
@ApiOperation(value = "查询某个应用的Pod列表")
@Data
@ToString
public class QueryPodListRequest {
    @ApiModelProperty(value = "环境分组")
    @NotEmpty
    private String namespaceName;
    @ApiModelProperty(value = "应用编码")
    @NotEmpty
    private String appCode;

    private String idc;

    private String ldc;
}
