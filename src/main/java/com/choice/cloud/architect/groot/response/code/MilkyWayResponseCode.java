package com.choice.cloud.architect.groot.response.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MilkyWayResponseCode implements ResponseInfo   {

    NO_APPINFO_OF_MILKYWAY("39999","银河中该应用不存在"),
    NO_CONFIGS_OF_APOLLO("39998","银河中应用配置项不存在"),
    NO_MAPPING_OF_APOLLO_APPID("39997", "银河中服务名和apollo appId 映射不存在"),
    NO_PERMISSION("39996", "您没有操作权限，请联系应用owner添加"),
    ;

    /** 状态码 */
    private final String code;

    /** 描述信息 */
    private final String desc;

}
