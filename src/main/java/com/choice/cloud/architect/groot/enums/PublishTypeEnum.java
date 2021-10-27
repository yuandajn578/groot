package com.choice.cloud.architect.groot.enums;

/**
 * <p>
 * 发布类型
 * </p>
 *
 * @author zhangkun
 */
public enum PublishTypeEnum {
    /**
     * 发布类型
     */
    DAILY("daily", "日常发布"),
    FAULT("fault", "故障发布"),
    DEVELOP("develop", "研发自测发布"),
    EMERGENCY("emergency", "非日常发布");

    private String value;

    private String description;

    private PublishTypeEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }


    public String value() {
        return value;
    }

    public static String description(String value) {
        for (PublishTypeEnum publishTypeEnum : PublishTypeEnum.values()) {
            if (publishTypeEnum.value.equals(value)) {
                return publishTypeEnum.description;
            }
        }
        return null;
    }

    public static PublishTypeEnum getEnumByValue(String value) {
        for (PublishTypeEnum publishTypeEnum : PublishTypeEnum.values()) {
            if (publishTypeEnum.value.equals(value)) {
                return publishTypeEnum;
            }
        }
        return FAULT;
    }
}
