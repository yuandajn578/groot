package com.choice.cloud.architect.groot.response.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApolloResponseCode implements ResponseInfo  {

    DUPLICATE_KEY_APOLLO("29999", "apollo配置中key重复"),
    DUPLICATE_KEY_DB("29998", "apollo变更单中key重复"),
    PK_ISEMPATY("29997","变更单业务主键为空"),
    NULL_NAMESPACE("29996","获取apollo namespace失败，请检查应用名是否与Apollo的AppId一致"),
    ITEM_HAS_DELETED("29997","该配置项已删除，请确认发布已经修改的配置项"),
    ENV_NOT_BLANK("29998","ENV不能为空或者ENV传值不正确"),
    ;

    /** 状态码 */
    private final String code;

    /** 描述信息 */
    private final String desc;
}
