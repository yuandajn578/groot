package com.choice.cloud.architect.groot.enums;

/**
 * <p>
 * 发布审核单状态
 * </p>
 *
 * @author zhangkun
 */
public enum PublishAuditEnum {
    /**
     * 发布审核单状态
     */
    TO_BE_COMMITTED("to_be_committed", "待提交"),
    PENDING_APPROVAL("pending_approval", "审批中"),
    CANCELED("canceled", "已取消"),
    APPROVED("approved", "审批通过"),
    REJECTED("rejected", "审批拒绝");

    private String value;

    private String description;

    PublishAuditEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }


    public String value() {
        return value;
    }

    public static String description(String value) {
        for (PublishAuditEnum publishAuditEnum : PublishAuditEnum.values()) {
            if (publishAuditEnum.value.equals(value)) {
                return publishAuditEnum.description;
            }
        }
        return null;
    }
}
