package com.choice.cloud.architect.groot.enums;

import lombok.Getter;

/**
 * <p>
 * Apollo更改历史类型枚举类
 * </p>
 *
 * @author LZ
 * @date Created in 2020/6/29 15:29
 */
@Getter
public enum ApolloCommitHistoryTypeEnum {
    CREATE("create", "新增"),
    UPDATE("update", "更新"),
    DELETE("delete", "删除"),
    ;

    private String code;
    private String desc;

    ApolloCommitHistoryTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
