package com.choice.cloud.architect.groot.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * <p>
 * 应用构建申请单Model
 * </p>
 *
 * @author zhangkun
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class GrApplicationForm extends BaseBizModel {

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
     * 审核id
     */
    private String auditorId;

    /**
     * 审核时间
     */
    private LocalDateTime auditorTime;
}
