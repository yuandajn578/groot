package com.choice.cloud.architect.groot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @ClassName EnvOnOffEnum
 * @Description TODO
 * @Author Guangshan Wang
 * @Date 2020/4/27/027 19:14
 */
@Getter
@AllArgsConstructor
public enum EnvOnOffEnum {
    online("online", "线上"),
    offline("offline","线下"),
    ;

    private String code;
    private String desc;
}
