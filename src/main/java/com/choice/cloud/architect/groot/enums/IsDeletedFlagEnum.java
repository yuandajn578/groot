package com.choice.cloud.architect.groot.enums;

import lombok.Getter;

/**
 * <p>
 * 是否删除标记枚举类
 * </p>
 *
 * @author LZ
 * @date Created in 2020/8/10 15:17
 */
@Getter
public enum IsDeletedFlagEnum {
    NOT_DELETED(0, "未删除"),
    IS_DELETED(1, "已删除"),
    ;

    IsDeletedFlagEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    private int code;
    private String name;
}
