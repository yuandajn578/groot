package com.choice.cloud.architect.groot.response.code;

/**
 * @classDesc: 状态码
 * @version: v1.0
 * @copyright: 北京辰森
 */
public enum SysResponseCode implements ResponseInfo {
    /**
     * Success
     */
    SUCCESS("10000", "SUCCESS"),
    /**
     * Error
     */
    ERROR("22000", "ERROR"),
    /**
     * Param Illegal
     */
    PARAM_ILLEGAL("21000", "请求参数不合法"),
    /**
     * Token validate error
     */
    TOKEN_VALIDATE_ERROR("23000", "Token 校验失败"),
    ;


    /**
     * 状态码
     */
    private final String code;
    /**
     * 描述信息
     */
    private final String desc;

    SysResponseCode(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}
