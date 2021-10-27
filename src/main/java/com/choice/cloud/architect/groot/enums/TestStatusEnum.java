package com.choice.cloud.architect.groot.enums;

/**
 * @author zhangkun
 */
public enum TestStatusEnum {
    /**
     * 测试状态
     */
    TESTING("testing", "待测试"),
    GREEN("green", "测试通过"),
    RED("red", "测试未通过");

    private String value;

    private String description;

    private TestStatusEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String value() {
        return value;
    }

    public static String description(String value) {
        for (TestStatusEnum testStatusEnum : TestStatusEnum.values()) {
            if (testStatusEnum.value.equals(value)) {
                return testStatusEnum.description;
            }
        }
        return null;
    }
}
