package com.choice.cloud.architect.groot.process.param;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: zhangguoquan
 * @Date: 2020/4/15 15:26
 */
@Data
@NoArgsConstructor
public class WebhookAt {

    private List<String> atMobiles = new ArrayList<>();

    private Boolean isAtAll = Boolean.FALSE;


    @Builder
    public WebhookAt(List<String> atMobiles, Boolean isAtAll) {
        this.atMobiles = atMobiles;
        this.isAtAll = isAtAll;
    }
}
