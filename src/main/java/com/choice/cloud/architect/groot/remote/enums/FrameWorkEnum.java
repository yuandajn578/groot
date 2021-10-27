package com.choice.cloud.architect.groot.remote.enums;

import lombok.Getter;

/**
 * @Author: zhangguoquan
 * @Date: 2020/5/6 14:41
 */
@Getter
public enum FrameWorkEnum {
    FW_MAVEN("maven", "maven"),
    FW_GRADLE("gradle", "gradle"),
    FW_JAR("jar", "jar"),
    FW_NULL_PUBLISH("nullPublish", "nullPublish"),
    ;

    private String code;
    private String desc;

    FrameWorkEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
