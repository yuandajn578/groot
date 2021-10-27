package com.choice.cloud.architect.groot.enums;

/**
 * @author zhangkun
 */
public enum PublishStrategyEnum {
    /**
     * 发布策略
     */
    ALL("all", "全部节点"),
    SPECIFY("specify", "指定节点");

    private String value;

    private String description;

    PublishStrategyEnum(String value, String description) {
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
        for (PublishStrategyEnum publishStrategyEnum : PublishStrategyEnum.values()) {
            if (publishStrategyEnum.value.equals(value)) {
                return publishStrategyEnum.description;
            }
        }
        return null;
    }
}
