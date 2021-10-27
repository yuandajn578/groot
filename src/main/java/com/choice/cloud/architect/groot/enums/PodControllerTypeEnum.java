package com.choice.cloud.architect.groot.enums;

/**
 * @author zhangkun
 */
public enum PodControllerTypeEnum {
    /**
     * Pod控制器类型
     */
    DEPLOYMENT("Deployment", "Deployment"),
    STATEFUL_SET("StatefulSet", "StatefulSet");

    private String value;

    private String description;

    PodControllerTypeEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String value() {
        return value;
    }

    public String description() {
        return description;
    }

    public static String description(String value) {
        for (PodControllerTypeEnum podControllerTypeEnum : PodControllerTypeEnum.values()) {
            if (podControllerTypeEnum.value.equals(value)) {
                return podControllerTypeEnum.description;
            }
        }
        return null;
    }
}
