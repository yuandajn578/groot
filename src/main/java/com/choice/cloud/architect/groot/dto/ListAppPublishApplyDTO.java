package com.choice.cloud.architect.groot.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * @author zhangkun
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class ListAppPublishApplyDTO extends BaseBizDTO{
    /**
     * 父级申请单编码
     */
    private String parentFormId;

    /**
     * 根申请单编码
     */
    private String rootFormId;

    /**
     * 申请单类型（1：开发构建单，2：提测单，3：灰度提测单，4：正式环境发布审核单，5：正式环境发布单）
     */
    private String formType;

    /**
     * 变更编码
     */
    private String changeId;

    /**
     * 环境编码
     */
    private String envId;

    /**
     * apollo发布环境
     */
    private String apolloEnv;

    /**
     * apollo发布idc
     */
    private String apolloIdc;

    /**
     * 描述信息
     */
    private String description;

    /**
     * 状态（0：关闭，1：开启）
     */
    private Integer status;

    /**
     * 删除标识(1：未删除 0：已删除)
     */
    private Integer deleteFlag;

    /**
     * 审核id
     */
    private String auditorId;

    /**
     * 审核时间
     */
    private LocalDateTime auditorTime;
}
