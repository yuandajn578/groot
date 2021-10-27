package com.choice.cloud.architect.groot.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/4 13:13
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@ApiModel("发布单详情model")
public class PublishInfoDTO extends BaseBizDTO{

    /**
     * 发布名称
     */
    @ApiModelProperty(value = "发布名称")
    private String publishName;

    /**
     * 关联的发布审核单
     */
    @ApiModelProperty(value = "关联的发布审核单Id")
    private String publishAuditId;

    /**
     * 关联的发布审核单名称
     */
    @ApiModelProperty(value = "关联的发布审核单名称")
    private String publishAuditName;

    /**
     * 变更id
     */
    @ApiModelProperty(value = "关联的变更单id")
    private String changeId;

    /**
     * 变更名称
     */
    @ApiModelProperty(value = "关联的变更名称")
    private String changeName;

    /**
     * 应用编码
     */
    @ApiModelProperty(value = "应用编码")
    private String appCode;

    /**
     * 应用名称
     */
    @ApiModelProperty(value = "应用名称")
    private String appName;

    /**
     * 架构域id
     */
    @ApiModelProperty(value = "架构域id")
    private String domainArchitectureId;

    /**
     * 架构域名称
     */
    @ApiModelProperty(value = "架构域名称")
    private String domainArchitectureName;

    /**
     * 环境
     */
    @ApiModelProperty(value = "发布环境")
    private String envType;

    /**
     * 环境编码
     */
    @ApiModelProperty(value = "发布环境编码")
    private String envCode;

    /**
     * 环境名称
     */
    @ApiModelProperty(value = "发布环境名称")
    private String envName;

    /**
     * 发布类型
     * daily_publish日常  /  emergency_release紧急发布
     */
    @ApiModelProperty(value = "发布类型（daily_publish日常  /  emergency_release紧急发布）")
    private String publishType;

    /**
     * 测试状态(已提测，测试中，已通过，未通过)
     * testing pass fail
     */
    @ApiModelProperty(value = "测试状态(untested、testing、green、red)")
    private String testStatus;

    /**
     * 发布状态 (待发布 发布中 成功 失败 关闭)
     * publishing success fail close
     */
    @ApiModelProperty(value = "测试状态(wait_publish、deploying、success、fail、closed)")
    private String publishStatus;

    /**
     * 开发人员姓名
     */
    @ApiModelProperty(value = "开发人员姓名")
    private String developerName;

    /**
     * 测试人员姓名
     */
    @ApiModelProperty(value = "测试人员姓名")
    private String qaName;

    @ApiModelProperty(value = "apollo环境")
    private String apolloEnv;

    @ApiModelProperty(value = "apollo idc")
    private String apolloIdc;

    /**
     * 服务分组（on/off）
     */
    @ApiModelProperty(value = "服务分组（on/off）")
    private String serviceGroupSwitch;

    /**
     * MQ_group (on/off)
     */
    @ApiModelProperty(value = "MQ分组（on/off）")
    private String mqGroupSwitch;

    @ApiModelProperty(value = "应用配置")
    private GrAppEnvDefaultConfigDTO config;
}
