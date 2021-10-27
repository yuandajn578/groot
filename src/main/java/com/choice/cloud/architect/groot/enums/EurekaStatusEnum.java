package com.choice.cloud.architect.groot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @ClassName EurekaStatusEnum
 * @Description TODO
 * @Author Guangshan Wang
 * @Date 2020/7/14/014 14:02
 */
@Getter
@AllArgsConstructor
public enum EurekaStatusEnum {

    UP("UP", "UP"),
    OUT_OFF_SERVICE("OUT_OF_SERVICE", "OUT_OF_SERVICE"),
    ;

    private String code;
    private String desc;
}
