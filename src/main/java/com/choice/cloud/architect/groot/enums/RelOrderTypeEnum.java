package com.choice.cloud.architect.groot.enums;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/13 19:32
 */
public enum RelOrderTypeEnum {

    /**
     * 关联类型
     */
    ORDER_PUBLISH("publish", "发布单据"),
    ORDER_CHANGE("change", "变更单据"),
    ;

    private String value;

    private String description;

    private RelOrderTypeEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }


    public String value() {
        return value;
    }

    public static String description(String value) {
        for (RelOrderTypeEnum relOrderTypeEnum : RelOrderTypeEnum.values()) {
            if (relOrderTypeEnum.value.equals(value)) {
                return relOrderTypeEnum.description;
            }
        }
        return null;
    }
}
