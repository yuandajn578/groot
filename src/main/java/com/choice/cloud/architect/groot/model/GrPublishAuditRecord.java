package com.choice.cloud.architect.groot.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author zhangkun
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class GrPublishAuditRecord extends BaseBizModel {

    /**
     * 发布审核单id
     */
    private String publishAuditId;

    /**
     * 发布审核单名称
     */
    private String publishAuditName;

    /**
     * 钉钉审批单id
     */
    private String dtalkProcessId;

    /**
     * 环境 线上 线下
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
     * 发布类型 daily_publish日常 emergency_release紧急发布
     */
    private String publishType;

    /**
     * 上一个状态 审核中 通过 拒绝
     */
    private String previousStatus;

    /**
     * 当前状态 审核中 通过 拒绝
     */
    private String currentStatus;
}
