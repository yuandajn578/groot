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
public class GrAppEnvRel extends BaseBizModel{
    /**
     * 应用编码
     */
    private String appCode;

    /**
     * 环境类型
     */
    private String envType;

    /**
     * 环境类型
     */
    private String envName;

    /**
     * 环境编码
     */
    private String envCode;

    /**
     * 变更id
     */
    private String changeId;

    public String getK8sNs() {
        return this.getEnvType().concat("-").concat(this.getEnvCode());
    }
}
