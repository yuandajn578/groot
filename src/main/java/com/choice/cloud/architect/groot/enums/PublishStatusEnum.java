package com.choice.cloud.architect.groot.enums;

/**
 * @author zhangkun
 */
public enum PublishStatusEnum {
    /**
     * 发布状态
     */
    WAIT_PUBLISH("wait_publish", "待发布"),
    DEPLOYING("deploying", "发布中"),
    SUCCESS("success", "发布成功"),
    FAILURE("failure", "发布失败"),
    WAITING("waiting", "等待执行器"),
    CLOSED("closed", "已关闭");

    private String value;

    private String description;

    PublishStatusEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }


    public String value() {
        return value;
    }

    public static String description(String value) {
        for (PublishStatusEnum publishStatusEnum : PublishStatusEnum.values()) {
            if (publishStatusEnum.value.equals(value)) {
                return publishStatusEnum.description;
            }
        }
        return null;
    }

}
