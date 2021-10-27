package com.choice.cloud.architect.groot.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @classDesc:
 * @author: yuxiaopeng
 * @createTime: 2020-02-22
 * @version: v1.0.0
 * @email: yuxiaopeng@choicesoft.com.cn
 */
@Getter
@Setter
@ToString
@EnableConfigurationProperties(ApplicationProperties.class)
@ConfigurationProperties(ApplicationProperties.APPLICATION_PROPERTIES_PREFIX)
public class ApplicationProperties {

    public static final String APPLICATION_PROPERTIES_PREFIX = "app.config";
    /**
     * token前缀
     */
    public static final String AUTH_TOKEN_PREFIX = "Bearer ";

    private Integer maxContentLength = 35;
    private String feignToken;
    private Boolean enableFeignLog = false;
    private String smsTemplateCode;
    private Integer messageContentLength = 2500;
}
