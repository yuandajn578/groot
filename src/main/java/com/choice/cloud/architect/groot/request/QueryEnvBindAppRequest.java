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
@ApiModel(value = "查询环境已关联的应用")
public class QueryEnvBindAppRequest {
    @ApiModelProperty(value = "环境类型")
    private String envType;
    @ApiModelProperty(value = "环境编码")
    private String envCode;
}
