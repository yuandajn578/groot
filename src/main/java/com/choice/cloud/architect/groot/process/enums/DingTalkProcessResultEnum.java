package com.choice.cloud.architect.groot.process.enums;

import lombok.Getter;

/**
 * @ClassName DingTalkProcessResultEnum
 * @Description 钉钉审批通过或者拒绝
 * @Author Guangshan Wang
 * @Date 2020/4/1/001 15:52
 */
@Getter
public enum DingTalkProcessResultEnum {

    REFUSE("refuse", "拒绝"),
    AGREE("agree", "同意"),
            ;
    private String code;
    private String desc;

    DingTalkProcessResultEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
