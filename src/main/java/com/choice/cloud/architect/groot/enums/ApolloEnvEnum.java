package com.choice.cloud.architect.groot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApolloEnvEnum {

    /**
     * 不要轻易修改
     */
    DEV("DEV", "开发"),
    FAT("FAT","测试"),
    PRE("PRE", "预发"),
    PRO("PRO", "生产");

    private String code;
    private String desc;
}
