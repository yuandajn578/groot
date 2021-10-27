package com.choice.cloud.architect.groot.process.enums;

import lombok.Getter;

/**
 * @ClassName PlatformTypeEnum
 * @Description 审批类型
 * @Author Guangshan Wang
 * @Date 2020/3/10/010 19:30
 */
@Getter
public enum ProcessTypeEnum {

    CREATE_ENV("create_env", "创建环境"),
    RELEASE_SYSTEM("release_system", "应用发布"),
    ;
    private String code;
    private String desc;
    ProcessTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
