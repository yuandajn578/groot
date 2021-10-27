package com.choice.cloud.architect.groot.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * @author zhangkun
 */
@Data
@ToString
@ApiModel(value = "应用环境详情")
public class AppEnvDetailDTO {
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
    @ApiModelProperty(value = "硬件规格Id")
    private String resourceSpecId;
    @ApiModelProperty(value = "硬件规则名称")
    private String resourceSpecName;
    @ApiModelProperty(value = "发布配置")
    private AppEnvPublishConfigDTO publishConfig;
}
