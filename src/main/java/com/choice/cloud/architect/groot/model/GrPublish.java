package com.choice.cloud.architect.groot.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class GrPublish extends BaseBizModel  {

    /**
     * 发布名称
     */
    private String publishName;

    /**
     * 关联的发布审核单
     */
    private String publishAuditId;

    /**
     * 关联的发布审核单名称
     */
    private String publishAuditName;

    /**
     * 变更id
     */
    private String changeId;

    /**
     * 变更名称
     */
    private String changeName;

    /**
     * 应用编码
     */
    private String appCode;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 架构域id
     */
    private String domainArchitectureId;

    /**
     * 架构域名称
     */
    private String domainArchitectureName;

    /**
     * 环境(线上/线下)
     */
    private String envType;

    /**
     * 环境编码
     */
    private String envCode;

    /**
     * 环境名称
     */
    private String envName;

    /**
     * 发布类型
     * daily日常  /  emergency紧急发布
     */
    private String publishType;

    /**
     * 测试状态(已提测，测试中，已通过，未通过)
     * testing pass fail
     */
    private String testStatus;

    /**
     * 发布状态 (待发布 发布中 成功 失败 关闭)
     * publishing success fail close
     */
    private String publishStatus;

    /**
     * 开发人员姓名
     */
    private String developerName;

    /**
     * 测试人员姓名
     */
    private String qaName;

    private String apolloEnv;

    private String apolloIdc;

    /**
     * 服务分组（on/off）
     */
    private String serviceGroupSwitch;

    /**
     * MQ_group (on/off)
     */
    private String mqGroupSwitch;

    /**
     * gitlab repo
     */
    private String gitRepo;

    /**
     * gitlab branch
     */
    private String gitBranch;

    /**
     * 操作人id
     */
    private String operatorId;

    /**
     * 发布时间
     */
    private LocalDateTime releaseTime;

    /**
     * jk 构建号
     */
    private Integer buildNum;

    /**
     * deployment 版本
     */
    private String deploymentRevision;
}
