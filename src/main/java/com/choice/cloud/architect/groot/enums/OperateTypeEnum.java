package com.choice.cloud.architect.groot.enums;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/14 9:50
 */
public enum OperateTypeEnum {

    DEPLOY("DEPLOY", "部署"),
    TEST("TEST", "测试");

    private String value;

    private String description;

    private OperateTypeEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }


    public String value() {
        return value;
    }

    public static String description(String value) {
        for (OperateTypeEnum operateTypeEnum : OperateTypeEnum.values()) {
            if (operateTypeEnum.value.equals(value)) {
                return operateTypeEnum.description;
            }
        }
        return null;
    }
}
