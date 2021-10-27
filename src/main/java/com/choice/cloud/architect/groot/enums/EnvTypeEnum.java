package com.choice.cloud.architect.groot.enums;

/**
 * <p>
 *     变更单所处阶段枚举类
 * </p>
 * @author zhangkun
 */
public enum EnvTypeEnum {
    dev("dev", "开发"),
    test("test", "测试"),
    pre("pre", "预发"),
    pro("pro", "生产");

    private String value;

    private String description;

    EnvTypeEnum(String value, String description) {
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
        for (EnvTypeEnum envTypeEnum : EnvTypeEnum.values()) {
            if (envTypeEnum.value.equals(value)) {
                return envTypeEnum.description;
            }
        }
        return null;
    }
}
