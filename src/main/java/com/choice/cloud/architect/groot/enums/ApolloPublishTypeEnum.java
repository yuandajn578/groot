package com.choice.cloud.architect.groot.enums;

import lombok.Getter;

/**
 * <p>
 * Apollo发布类型枚举类
 * </p>
 *
 * @author LZ
 * @date Created in 2020/6/16 13:31
 */
@Getter
public enum ApolloPublishTypeEnum {
    NO_CHANGE_ITEM(0, ""),
    UPDATE_ITEM(1, "修改"),
    ADD_ITEM(2, "新增"),
    DELETE_ITEM(3, "删除"),
    ;

    private Integer code;
    private String desc;

    ApolloPublishTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
