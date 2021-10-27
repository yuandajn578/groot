package com.choice.cloud.architect.groot.enums;

import lombok.Getter;

/**
 * <p>
 * apollo 配置项发布状态枚举类
 * </p>
 *
 * @author LZ
 * @date Created in 2020/6/15 16:14
 */
@Getter
public enum ApolloItemStatusEnum {
    WAITING_PUBLISH(0, "待发布"),
    PUBLISH_ED(1, "已发布"),
    ;

    ApolloItemStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private Integer code;
    private String desc;
}
