package com.choice.cloud.architect.groot.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

/**
 * @author zhangkun
 */
@Data
@ToString
@ApiModel(value = "环境分组信息")
public class ListAppEnvDTO {
    @ApiModelProperty(value = "Id")
    private String oid;
    @ApiModelProperty(value = "环境类型")
    private String envType;
    @ApiModelProperty(value = "环境编码")
    private String envCode;
    @ApiModelProperty(value = "环境名称")
    private String envName;
    @ApiModelProperty(value = "有效期至")
    private Date expireDate;
    @ApiModelProperty(value = "审核状态")
    private String auditStatus;
}
