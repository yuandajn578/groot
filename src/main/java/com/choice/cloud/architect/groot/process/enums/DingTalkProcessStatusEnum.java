package com.choice.cloud.architect.groot.process.enums;

import lombok.Getter;

/**
 * @ClassName DingTalkProcessStatusEnum
 * @Description 钉钉审批流的状态枚举
 * @Author Guangshan Wang
 * @Date 2020/3/10/010 8:35
 */
@Getter
public enum DingTalkProcessStatusEnum {

    FINISH("finish", "正常结束"),
    START("start", "开启"),
    TERMINATE("terminate", "终止/撤回"),

    ;
    private String code;
    private String desc;

    DingTalkProcessStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
