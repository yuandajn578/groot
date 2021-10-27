package com.choice.cloud.architect.groot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/5 16:48
 */
@Getter
@AllArgsConstructor
public enum SwitchEnum {

    ON("on", "打开"),
    OFF("off", "关闭");

    private String value;

    private String description;
}
