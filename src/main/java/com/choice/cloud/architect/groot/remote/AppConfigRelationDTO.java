package com.choice.cloud.architect.groot.remote;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName AppConfigRelationDTO
 * @Description 应用-配置项 关联信息
 * @Author Guangshan Wang
 * @Date 2020/2/13/013 1:27
 */
@Data
@ApiModel(description = "配置项关联参数")
public class AppConfigRelationDTO {

    @ApiModelProperty(hidden = true)
    private Integer id;

    @ApiModelProperty(value = "业务ID")
    private String oid;

    @ApiModelProperty(value = "应用code")
    private String appCode;

    @ApiModelProperty(value = "应用名称")
    private String appName;

    @ApiModelProperty(value = "配置ID")
    private String configId;

    @ApiModelProperty(value = "配置名称")
    private String configName;

    @ApiModelProperty(value = "配置值")
    private String value;

    @ApiModelProperty(value = "版本")
    private String version;

    @ApiModelProperty(value = "创建人")
    private String createUser;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改人")
    private String updateUser;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    @ApiModelProperty(value = "删除标志")
    private Boolean deleteFlag;
}
