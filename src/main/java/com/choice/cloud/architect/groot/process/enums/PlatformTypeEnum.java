package com.choice.cloud.architect.groot.process.enums;

import lombok.Getter;

/**
 * @ClassName PlatformTypeEnum
 * @Description 平台类型
 * @Author Guangshan Wang
 * @Date 2020/3/10/010 19:30
 */
@Getter
public enum PlatformTypeEnum {

    WECHAT_PLATFORM("wechat", "微信平台"),
    DINGTALK_PLATFORM("dingtalk", "钉钉平台"),
    ;
    private String code;
    private String desc;
    PlatformTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
