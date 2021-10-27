package com.choice.cloud.architect.groot.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/3 19:03
 */
@Data
@ApiModel(description = "发布单更新")
public class UpdatePublishRequest {

    @ApiModelProperty(value = "发布单oid")
    @NotBlank(message = "oid 不能为空")
    private String oid;

    @ApiModelProperty(value = "服务分组是否开启")
    private String serviceGroupSwitch;

    @ApiModelProperty(value = "MQ分组是否开启")
    private String mqGroupSwitch;

    @ApiModelProperty(value = "发布状态")
    private String publishStatus;

}
