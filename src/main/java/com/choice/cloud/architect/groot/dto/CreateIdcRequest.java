package com.choice.cloud.architect.groot.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * @ClassName CreateIdcRequest
 * @Description 创建idc请求
 * @Author LZ
 * @Date 2020/4/10 18:54
 * @Version 1.0
 */
@Data
public class CreateIdcRequest {
    @NotBlank(message = "应用id列表appIds不能为空，用英文逗号隔开的字符串")
    private String appIds;
    @NotBlank(message = "cookie不能为空，区分线上和线下")
    private String cookie;
    @NotBlank(message = "env不能为空，线上PRO，线下PRE")
    private String env;
}
