package com.choice.cloud.architect.groot.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 应用变更Model
 * </p>
 *
 * @author zhangkun
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class GrChange extends BaseBizModel {

    /** 变更审核单ID */
    private String auditingId;
    /**
     * 变更描述
     */
    private String changeName;

    /**
     * 应用编码(银河)
     */
    private String appId;

    /** 应用编码 */
    private String appCode;

    /** 服务名对应的apollo的appId */
    private String apolloAppCode;

    /**
     * 中文应用名称(银河)
     */
    private String appName;

    /**
     * 仓库地址
     */
    private String gitlabAddress;

    /**
     * 分支名称
     */
    private String branchName;

    /** 克隆分支 */
    private String cloneBranch;

    /** 克归属项目 */
    private String belongProject;

    /** 归属变更集 */
    private String belongChangeset;

    /** 计划发布时间 */
    private LocalDateTime publishTime;

    /** 发布后是否删除分支 */
    private Integer  deleteBranch;

    /**
     * apollo对应应用id
     */
    private String apolloApplicationId;

    /**
     * sprint地址
     */
    private String sprintAddress;

    /**
     * sprint提测单地址
     */
    private String sprintTestApplicationAddress;

    /**
     * 描述信息
     */
    private String description;

    /** 变更单所处的阶段 */
    private String stage;

    /** 数据版本 */
    private Integer version;

    /**
     * 研发人员
     */
    private String developer;

    /**
     * 测试人员
     */
    private String tester;

    private String operatorId;

    private String iterationId;

    /** 搜索条件 */
    private String searchValue;

    private List<String> ids;

    /**
     * cr人员列表
     */
    private List<GrMember> crMembers;
}
