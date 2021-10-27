package com.choice.cloud.architect.groot.process.code;

import com.choice.cloud.architect.groot.response.code.ResponseInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountResponseCode implements ResponseInfo  {

    NO_BIND_DING_TALK("30001", "没有绑定钉钉账户"),
    START_DING_TALK_PROCESS_ERROR("30002", "发起钉钉审批流失败"),
    ;

    /** 状态码 */
    private final String code;

    /** 描述信息 */
    private final String desc;
}
