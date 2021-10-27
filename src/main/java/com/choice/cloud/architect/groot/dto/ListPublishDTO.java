package com.choice.cloud.architect.groot.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/3 17:22
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@ApiModel("发布单列表model")
public class ListPublishDTO extends BaseBizDTO{

    @ApiModelProperty(value = "发布序号")
    private Integer id;

    @ApiModelProperty(value = "发布名称")
    private String publishName;

    @ApiModelProperty(value = "应用名称")
    private String appName;

    @ApiModelProperty(value = "变更名称")
    private String changeName;

    @ApiModelProperty(value = "应用编码")
    private String appCode;

    @ApiModelProperty(value = "应用所属项目（架构域）名称")
    private String domainArchitectureName;

    @ApiModelProperty(value = "git分支")
    private String gitBranch;

    @ApiModelProperty(value = "开发者姓名")
    private String developerName;

    @ApiModelProperty(value = "测试者姓名")
    private String qaName;

    @ApiModelProperty(value = "测试状态(wait_publish、deploying、success、fail、closed)")
    private String testStatus;

    @ApiModelProperty(value = "发布环境")
    private String envType;

    @ApiModelProperty(value = "发布环境名称")
    private String envName;

    @ApiModelProperty(value = "测试状态(wait_publish、deploying、success、fail、closed)")
    private String publishStatus;

    @ApiModelProperty(value = "关联的发布审核单名称")
    private String publishAuditName;

    @ApiModelProperty(value = "关联的发布审核单名称")
    private LocalDateTime releaseTime;

    @ApiModelProperty(value = "关联的发布审核单名称")
    private Integer buildNum;

    /**
     * deployment 版本
     */
    private String deploymentRevision;

}
