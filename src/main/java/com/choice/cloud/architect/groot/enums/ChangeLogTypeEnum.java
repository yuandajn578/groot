package com.choice.cloud.architect.groot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChangeLogTypeEnum {

    APOLLO("阿波罗"),
    DB("数据库"),
    ;

    private String desc;
}
