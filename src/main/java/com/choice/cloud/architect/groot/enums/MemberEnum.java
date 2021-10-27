package com.choice.cloud.architect.groot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberEnum {

    PRODUCTOR("产品"),
    DEVELOPER("开发"),
    CODEREVIEW("CR"),
    CONNER("测试");

    private String desc;

}
