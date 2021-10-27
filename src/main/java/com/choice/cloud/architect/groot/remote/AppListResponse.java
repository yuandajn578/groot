package com.choice.cloud.architect.groot.remote;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@ApiModel(description = "应用参数")
public class AppListResponse {

    @ApiModelProperty(hidden = true)
    private Integer id;

    @ApiModelProperty(value = "应用ID")
    private String oid;

    @ApiModelProperty(value = "应用编码")
    private String appCode;

    @ApiModelProperty(value = "应用名称")
    private String appName;

    @ApiModelProperty(value = "应用状态")
    private String status;

    @ApiModelProperty(value = "应用类型")
    private String appType;


    @ApiModelProperty(value = "应用等级")
    private String appRank;

    @ApiModelProperty(value = "架构域ID")
    private String domainArchitectureId;

    @ApiModelProperty(value = "架构域名称")
    private String domainArchitectureName;

    @ApiModelProperty(value = "ownerID")
    private String ownerId;

    @ApiModelProperty(value = "owner名称")
    private String ownerName;

    @ApiModelProperty(value = "git地址")
    private String gitAddress;

    @ApiModelProperty(value = "数据库地址")
    private String dbAddress;

    @ApiModelProperty(value = "Dockfile path")
    private String jarFilePath;

    @ApiModelProperty(value = "构建方式 maven gradle")
    private String framework;

    @ApiModelProperty(value = "springboot版本")
    private String springboot;

    @ApiModelProperty(value = "springcloud版本")
    private String springcloud;

    @ApiModelProperty(value = "应用描述")
    private String appDesc;

    @ApiModelProperty(value = "创建人")
    private String createUser;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "修改人")
    private String updateUser;

    @ApiModelProperty(value = "修改时间")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "删除标志")
    private Boolean deleteFlag;


    /**
     * 应用关联成员列表
     */
    @ApiModelProperty(value = "应用关联成员列表")
    private Map<String, List<AppRoleRelationDTO>> memberMap;
    /**
     * 应用关联配置项列表
     */
    @ApiModelProperty(value = "应用关联配置项列表")
    private  List<AppConfigRelationDTO> configOptionList;
}
