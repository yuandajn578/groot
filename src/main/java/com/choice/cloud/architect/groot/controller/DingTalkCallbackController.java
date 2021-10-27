package com.choice.cloud.architect.groot.controller;

import com.choice.cloud.architect.groot.response.ResponseData;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * TODO 钉钉审批回调注册
 * </p>
 *
 * @author zhangkun
 */
@RestController
@RequestMapping("/api")
public class DingTalkCallbackController {
    /**
     * 钉钉审批回调
     */
    @PostMapping("/processInstance/callback")
    public ResponseData dingTalkProcessInstanceCallback() {
        return ResponseData.createBySuccess();
    }
}
