package com.choice.cloud.architect.groot.request;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

/**
 * @author zhangkun
 */
@ApiOperation(value = "查询某个应用的Pod列表(ldc维度)")
@Data
@ToString
public class QueryLdcPodListRequest {
    @ApiModelProperty(value = "环境类型")
    @NotEmpty
    private String envType;

    @ApiModelProperty(value = "环境编码")
    @NotEmpty
    private String envCode;

    @ApiModelProperty(value = "应用编码")
    @NotEmpty
    private String appCode;
}
