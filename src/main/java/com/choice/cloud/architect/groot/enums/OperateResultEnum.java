package com.choice.cloud.architect.groot.enums;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/14 9:59
 */
public enum OperateResultEnum {

    SUCCESS("success", "成功"),
    FAILURE("failure", "失败");

    private String value;

    private String description;

    private OperateResultEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }


    public String value() {
        return value;
    }

    public static String description(String value) {
        for (OperateResultEnum operateResultEnum : OperateResultEnum.values()) {
            if (operateResultEnum.value.equals(value)) {
                return operateResultEnum.description;
            }
        }
        return null;
    }
}
