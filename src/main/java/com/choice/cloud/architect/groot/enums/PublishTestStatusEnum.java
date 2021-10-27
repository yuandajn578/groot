package com.choice.cloud.architect.groot.enums;

import lombok.Getter;

/**
 * @ClassName PublishTestStatusEnum
 * @Description 发布单的测试状态枚举
 * @Author Guangshan Wang
 * @Date 2020/3/17/017 10:54
 */
@Getter
public enum PublishTestStatusEnum {

    GREEN("green", "测试通过"),
    RED("red", "测试未通过"),
    TESTING("testing", "待测试"),
    ;

    private String code;
    private String desc;

    PublishTestStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 通过code 查询测试状态枚举
     * @param code
     * @return
     */
    public static PublishTestStatusEnum findByCode(String code) {
        for (PublishTestStatusEnum statusEnum : PublishTestStatusEnum.values()) {
            if (statusEnum.getCode().equals(code)) {
                return statusEnum;
            }
        }
        return null;
    }

}
