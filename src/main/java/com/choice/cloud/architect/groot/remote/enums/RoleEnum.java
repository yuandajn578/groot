package com.choice.cloud.architect.groot.remote.enums;

import lombok.Getter;
import org.apache.commons.lang.StringUtils;

/**
 * @ClassName RoleEnum
 * @Description 角色枚举
 * @Author Guangshan Wang
 * @Date 2020/2/11/011 22:31
 */
@Getter
public enum RoleEnum {

    AppOps("AppOps", "AppOps"),
    RD("RD", "开发负责人"),
    publish("publish", "发布负责人"),
    QA("QA", "测试负责人"),
    QA_TL("QA_TL", "测试负责人leader"),
    DBA("DBA", "DBA"),
    SE("SE", "安全"),
    SRE("SRE", "运维"),
    PE("PE", "PE"),
    ;

    private String code;
    private String desc;

    RoleEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static RoleEnum getByCode(String code) {
        for (RoleEnum e : values()) {
            if (StringUtils.equalsIgnoreCase(e.getCode(), code)) {
                return e;
            }
        }
        return null;
    }
}
