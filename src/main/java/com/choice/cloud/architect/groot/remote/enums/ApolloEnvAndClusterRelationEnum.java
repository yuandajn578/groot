package com.choice.cloud.architect.groot.remote.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

/**
 * @ClassName ApolloEnvAndClusterRelationEnum
 * @Description Apollo的env和cluster（idc）映射枚举
 * @Author LZ
 * @Date 2020/4/8 14:48
 * @Version 1.0
 */
@Getter
public enum ApolloEnvAndClusterRelationEnum {
    FAT_DEV("DEV", "FAT", "DEV"),
    FAT_DEFAULT("FAT", "FAT", "DEFAULT"),
    PRO_PRE("PRE", "PRO", "PRE"),
    PRO_DEFAULT("PRO", "PRO", "DEFAULT")
    ;

    private String envParam;
    private String env;
    private String cluster;

    ApolloEnvAndClusterRelationEnum(String envParam, String env, String cluster) {
        this.envParam = envParam;
        this.env = env;
        this.cluster = cluster;
    }

    public static ApolloEnvAndClusterRelationEnum getEnumByEnvParam(String envParam) {
        Assert.hasLength(envParam, "env param not null or blank");
        for (ApolloEnvAndClusterRelationEnum e : values()) {
            if (StringUtils.equalsIgnoreCase(e.getEnvParam(), envParam)) {
                return e;
            }
        }
        return null;
    }
}
