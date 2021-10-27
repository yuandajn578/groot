package com.choice.cloud.architect.groot.request;

import com.choice.cloud.architect.groot.dto.AppEnvPublishConfigDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

/**
 * @author zhangkun
 */
@Data
@ToString
@ApiModel(description = "编辑环境")
public class EditEnvRequest {
    @ApiModelProperty(value = "oid")
    @NotEmpty
    private String oid;

    @NotEmpty
    @ApiModelProperty(value = "appCode")
    private String appCode;

    private String envCode;

    private String envType;

    /**
     * 环境中文描述
     */
    @ApiModelProperty(value = "环境名称")
    @NotEmpty
    private String envName;

    /**
     * 描述信息
     */
    @ApiModelProperty(value = "备注")
    private String description;

    /**
     * 到期时间
     */
    @ApiModelProperty(value = "到期时间")
    private Date expireDate;

    /**
     * 硬件资源规格
     */
    @ApiModelProperty(value = "硬件资源规格")
    private String resourceSpecId;

    /**
     * 发布配置
     */
    @ApiModelProperty(value = "发布配置")
    private AppEnvPublishConfigDTO publishConfig;
}
