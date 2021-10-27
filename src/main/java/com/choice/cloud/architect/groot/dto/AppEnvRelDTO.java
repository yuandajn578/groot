package com.choice.cloud.architect.groot.dto;

import lombok.Data;

/**
 * @Author: zhangguoquan
 * @Date: 2020/7/13 15:59
 */
@Data
public class AppEnvRelDTO {
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
}
