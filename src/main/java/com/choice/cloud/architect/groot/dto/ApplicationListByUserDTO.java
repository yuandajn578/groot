package com.choice.cloud.architect.groot.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/19 14:07
 */
@Data
@ApiModel("发布应用下拉列表")

public class ApplicationListByUserDTO {

    @ApiModelProperty(hidden = true)
    private Integer id;

    @ApiModelProperty(value = "应用ID")
    private String oid;

    @ApiModelProperty(value = "应用编码")
    private String appCode;

    @ApiModelProperty(value = "应用名称")
    private String appName;
}
