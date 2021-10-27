package com.choice.cloud.architect.groot.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * @author zhangkun
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString
@ApiModel(value = "发布审核单列表Model")
public class ListPublishAuditDTO extends BaseBizDTO{

    private Integer id;
    /**
     * 发布名称
     */
    @ApiModelProperty(value = "名称")
    private String publishName;

    /**
     * 关联应用
     */
    @ApiModelProperty(value = "关联应用")
    private String publishApps;

    /**
     * 关联问题（可以填jira地址）
     */
    @ApiModelProperty(value = "关联问题")
    private String aboutProblem;

    /**
     * 环境类型 线上 线下
     */
    @ApiModelProperty(value = "环境类型")
    private String envType;

    /**
     * 环境编码
     */
    @ApiModelProperty(value = "环境编码")
    private String envCode;

    /**
     * 环境名称
     */
    @ApiModelProperty(value = "环境名称")
    private String envName;

    /**
     * 发布类型 daily日常 emergency紧急发布
     */
    @ApiModelProperty(value = "发布类型 daily日常发布 emergency紧急发布")
    private String publishType;

    /**
     * 状态 审核中 已通过 已拒绝
     */
    @ApiModelProperty(value = "状态 (to_be_committed:待提交,pending_approval:审批中,canceled:已取消,approved:审批通过,rejected:审批拒绝）")
    private String auditStatus;

    /**
     * 描述信息
     */
    private String publishDesc;

    /**
     * 预计发布时间
     */
    private LocalDateTime expectPublishTime;

    /**
     * dtalk id
     */
    private String dTalkProcessId;
}
