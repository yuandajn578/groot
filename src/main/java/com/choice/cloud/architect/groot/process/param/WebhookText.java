package com.choice.cloud.architect.groot.process.param;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: zhangguoquan
 * @Date: 2020/4/15 15:23
 */
@Data
@NoArgsConstructor
public class WebhookText {

    private String content;

    @Builder
    public WebhookText(String content) {
        this.content = content;
    }

}
