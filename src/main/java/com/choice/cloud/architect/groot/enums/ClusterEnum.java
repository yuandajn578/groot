package com.choice.cloud.architect.groot.enums;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/11 13:57
 */
public enum ClusterEnum {

    ONLINE("online", "线上"),
    OFFLINE("offline", "线下");

    private String value;

    private String description;

    private ClusterEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }


    public String value() {
        return value;
    }

    public String description() {
        return description;
    }
}
