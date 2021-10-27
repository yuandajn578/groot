package com.choice.cloud.architect.groot.remote.enums;

import lombok.Getter;
import org.apache.commons.lang.StringUtils;

/**
 * @ClassName RoleEnum
 * @Description 应用等级枚举
 * @Author Guangshan Wang
 * @Date 2020/2/11/011 22:31
 */
@Getter
public enum AppRankEnum {

    COMMON_APP("COMMON_APP", "普通应用"),
    CORE_APP("CORE_APP", "核心应用"),
    ;

    private String code;
    private String desc;

    AppRankEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static AppRankEnum getByCode(String code) {
        for (AppRankEnum e : values()) {
            if (StringUtils.equalsIgnoreCase(e.getCode(), code)) {
                return e;
            }
        }
        return null;
    }
}