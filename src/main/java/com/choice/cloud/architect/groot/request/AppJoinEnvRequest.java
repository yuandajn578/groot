package com.choice.cloud.architect.groot.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author zhangkun
 */
@ApiModel(value = "关联应用保存接口")
@Data
@ToString
public class AppJoinEnvRequest {
    @ApiModelProperty(value = "环境类型")
    private String envType;
    @ApiModelProperty(value = "环境编码")
    private String envCode;
    @ApiModelProperty(value = "应用编码")
    private String appCode;
}
