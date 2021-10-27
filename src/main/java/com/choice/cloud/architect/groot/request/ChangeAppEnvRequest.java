package com.choice.cloud.architect.groot.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/27 16:47
 */
@Data
public class ChangeAppEnvRequest {

    @NotBlank(message = "appCode不能为空")
    private String appCode;

    @NotBlank(message = "changeId不能为空")
    private String changeId;

    @NotBlank(message = "envType不能为空")
    private String envType;
}
