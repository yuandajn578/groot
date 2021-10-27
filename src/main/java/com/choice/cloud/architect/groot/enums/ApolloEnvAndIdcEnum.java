package com.choice.cloud.architect.groot.enums;

import lombok.Getter;

/**
 * <p>
 * Apollo 环境和集群映射枚举类
 * </p>
 *
 * @author LZ
 * @date Created in 2020/5/25 16:11
 */
@Getter
public enum ApolloEnvAndIdcEnum {
    DEV("FAT", "DEV"),
    FAT("FAT", "DEFAULT"),
    PRE("PRO", "PRE"),
    PRO("PRO", "DEFAULT"),
    ;

    private String env;
    private String idc;

    ApolloEnvAndIdcEnum(String env, String idc) {
        this.env = env;
        this.idc = idc;
    }
}
