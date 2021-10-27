package com.choice.cloud.architect.groot.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @Author: zhangguoquan
 * @Date: 2020/5/15 15:20
 */
@Data
public class AppRedeployRequest {

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
