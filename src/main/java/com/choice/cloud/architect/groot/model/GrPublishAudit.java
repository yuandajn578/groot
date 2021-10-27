package com.choice.cloud.architect.groot.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zhangkun
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class GrPublishAudit extends BaseBizModel {
    /**
     * 钉钉审批单id
     */
    private String dtalkProcessId;

    /**
     * 发布名称
     */
    private String publishName;

    /**
     * 关联问题（可以填jira地址）
     */
    private String aboutProblem;

    /**
     * 环境类型 线上 线下
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
     * 发布类型 daily日常 emergency紧急发布
     */
    private String publishType;

    /**
     * 状态 审核中 已通过 已拒绝
     */
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
     * 变更单集合
     */
    private List<String> changeIds;

    /**
     * 发起者
     */
    private String initiatorId;

    private String initiatorName;

    /**
     * 发布申请记录链接
     */
    private String publishApplyUrl;

    /**
     * 是否有数据库变更  1 有  0  没有
     */
    private Integer dbChangeFlag;
}
