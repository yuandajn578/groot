package com.choice.cloud.architect.groot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/25 18:17
 */
@Getter
@AllArgsConstructor
public enum BlackTypeEnum {

    WEEK("星期"),
    DATE("日期");


    private String desc;
}
