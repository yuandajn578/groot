package com.choice.cloud.architect.groot.gitlab.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GitMergeStatusEnum {

    /**
     * 不要轻易修改
     */
    UNCHECKED("unchecked", "进行中"),
    CAN_BE_MERGED("can_be_merged","可以合并"),
    CANNOT_BE_MERGED("cannot_be_merged", "不可合并"),
    ;

    private String code;
    private String desc;
}
