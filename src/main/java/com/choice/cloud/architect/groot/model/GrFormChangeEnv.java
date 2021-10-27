package com.choice.cloud.architect.groot.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * <p>
 * 应用构建申请单 - 应用变更 - 应用环境之间的关系Model
 * </p>
 *
 * @author zhangkun
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class GrFormChangeEnv extends BaseBizModel {

    /**
     * 申请单编码
     */
    private String formId;

    /**
     * 变更编码
     */
    private String changeId;

    /**
     * 环境编码
     */
    private String envId;

    /**
     * 审核id
     */
    private String auditorId;

    /**
     * 审核时间
     */
    private LocalDateTime auditorTime;
}
