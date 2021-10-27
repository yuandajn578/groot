package com.choice.cloud.architect.groot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 变更单状态枚举类
 */
@Getter
@AllArgsConstructor
public enum ChangeEnum {

    RUNNING(0, "进行中"),
    PUBLISHING(1,"待发布"),
    RELEASED(2,"已发布线上"),
    CLOSED(3, "已关闭"),
    TESTED(4,"已验收通过"),
    TEST_FAIL(5,"验收未通过待重发");

    private Integer code;

    private String desc;
}
