package com.choice.cloud.architect.groot.response.code;

/**
 * @author zhangkun
 */
public enum PublishAuditResponseCode implements ResponseInfo {
    /**
     *
     */
    SAME_APP_CHANGE("40000", "一次审核不可以出现多个相同应用的变更单"),
    CR_NOT_FOUND("40001", "变更单未添加code review人员，请去变更编辑页面添加");

    /**
     * 状态码
     */
    private final String code;
    /**
     * 描述信息
     */
    private final String desc;

    PublishAuditResponseCode(String code, String desc) {
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
