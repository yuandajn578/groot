package com.choice.cloud.architect.groot.remote.milkyway;

import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * <p>
 * 根据应用编码查询应用配置信息请求体
 * </p>
 *
 * @author LZ
 * @date Created in 2020/8/14 13:53
 */
@Data
public class AppConfigInfoRequestDTO {
    @NotBlank(message = "应用编码不能为空")
    private String appCode;
}
