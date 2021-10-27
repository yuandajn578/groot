package com.choice.cloud.architect.groot.dto;

import com.choice.cloud.architect.groot.enums.MemberEnum;
import com.choice.cloud.architect.groot.model.GrMember;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zhangkun
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class ListChangeDTO extends BaseBizDTO {

    private Integer id;
    /**
     * 变更描述
     */
    private String changeName;

    /**
     * 应用ID(银河)
     */
    private String appId;

    /** 应用Code(银河) */
    private String appCode;

    /** 应用ID(apollo) */
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

    /** 克隆分支 */
    @ApiModelProperty(value = "克隆分支")
    private String cloneBranch;

    /** 克归属项目 */
    @ApiModelProperty(hidden = true,value = "克归属项目")
    private String belongProject;

    /** 归属变更集 */
    @ApiModelProperty(hidden = true,value = "归属变更集")
    private String belongChangeset;

    /** 计划发布时间 */
    @ApiModelProperty(value = "计划发布时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;

    /** 发布后是否删除分支 */
    @ApiModelProperty(value = "发布后是否删除分支")
    private Integer  deleteBranch;

    @ApiModelProperty(value = "开发者")
    private String developer;

    @ApiModelProperty(value = "测试者")
    private String conner;

    /** 变更审核单ID */
    private String auditingId;
    /** 阶段 */
    private String stage;

    @ApiModelProperty(value = "开发者对象列表")
    private List<GrMember> developerMembers;

    @ApiModelProperty(value = "测试者对象列表")
    private List<GrMember> connerMembers;

    @ApiModelProperty(value = "CR人员列表")
    private List<GrMember> crMembers;

}
