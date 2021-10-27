package com.choice.cloud.architect.groot.process.param;

import lombok.Data;

/**
 * @Author: zhangguoquan
 * @Date: 2020/4/15 15:21
 */
@Data
public class WebhookTextParam {

    private String msgtype = "text";

    private WebhookText text;

    private WebhookAt at;
}
