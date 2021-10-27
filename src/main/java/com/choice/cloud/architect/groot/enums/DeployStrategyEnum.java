package com.choice.cloud.architect.groot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/5 15:09
 */
@Getter
@AllArgsConstructor
public enum DeployStrategyEnum {

    START_FIRST("startFirst", "先启动新pod再停止旧pod"),
    STOP_FIRST("stopFirst", "先停止旧pod再启动新pod"),
    DELETE_ALL_FIRST("deleteAllFirst", "删除所有pod再启动新pod");

    private String value;

    private String description;
}
