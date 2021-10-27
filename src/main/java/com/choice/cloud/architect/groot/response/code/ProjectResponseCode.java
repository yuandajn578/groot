package com.choice.cloud.architect.groot.response.code;

/**
 * <p>
 * 项目相关响应code
 * </p>
 */
public enum ProjectResponseCode implements ResponseInfo {
    /**
     *项目相关响应code
     */
    NOT_REJECT("100000", ""),
    ;

    /**
     * 状态码
     */
    private final String code;
    /**
     * 描述信息
     */
    private final String desc;

    ProjectResponseCode(String code, String desc) {
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
