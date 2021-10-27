package com.choice.cloud.architect.groot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/6 14:10
 */
@Getter
@AllArgsConstructor
public enum BuildResultEnum {

    BUILDING("BUILDING", "构建中"),
    SUCCESS("SUCCESS", "成功"),
    FAIL("FAIL", "失败");

    private String value;

    private String description;
}
