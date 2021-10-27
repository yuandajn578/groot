package com.choice.cloud.architect.groot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChangeOptEnum {

    ADD("新增"),
    DELETE("删除"),
    UPDATE("修改"),
    PUBLISH("发布");

    private String desc;
}
