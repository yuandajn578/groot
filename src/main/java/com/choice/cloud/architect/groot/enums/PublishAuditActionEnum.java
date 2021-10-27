package com.choice.cloud.architect.groot.enums;

/**
 * @author zhangkun
 */
public enum PublishAuditActionEnum {
    /**
     * 测试状态
     */
    COMMIT("commit", "提交"),
    CANCEL("cancel", "撤销"),
    AUDIT("audit", "审批");

    private String value;

    private String description;

    private PublishAuditActionEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }


    public String value() {
        return value;
    }

    public String description() {
        return description;
    }
}
