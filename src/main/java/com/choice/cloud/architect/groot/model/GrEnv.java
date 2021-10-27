package com.choice.cloud.architect.groot.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;

/**
 * <p>
 * 应用环境Model
 * </p>
 *
 * @author zhangkun
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class GrEnv extends BaseBizModel {

    /**
     * 环境中文描述
     */
    private String envName;

    /**
     * 环境编码（英文）
     */
    private String envCode;

    /**
     * 环境是否隔离（0：否，1：是）
     */
    private Integer envIsolation;

    /**
     * 环境类型
     */
    private String envType;

    /**
     * jvm启动环境参数
     */
    private String jvmParameter;

    /**
     * 实例个数
     */
    private String instanceNum;

    /**
     * cpu核数
     */
    private String cpuNum;

    /**
     * 描述信息
     */
    private String description;

    /**
     * 审核状态
     */
    private String auditStatus;

    /**
     * 失效日期
     */
    private Date expireDate;

    /**
     * 资源规格id
     */
    private String resourceSpecId;
}
