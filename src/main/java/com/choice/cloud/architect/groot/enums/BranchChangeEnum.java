package com.choice.cloud.architect.groot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BranchChangeEnum {

    CREATE("create", "创建新分支"),
    SELECT("select","选择已有分支"),
    ;

    private String code;

    private String desc;
}
