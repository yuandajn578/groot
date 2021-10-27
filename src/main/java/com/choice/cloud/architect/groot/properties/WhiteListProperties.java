package com.choice.cloud.architect.groot.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.List;

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
@EnableConfigurationProperties(WhiteListProperties.class)
@ConfigurationProperties(WhiteListProperties.WHITE_LIST_PROPERTIES_PREFIX)
public class WhiteListProperties {

    public static final String WHITE_LIST_PROPERTIES_PREFIX = "request.white";

    private List<String>  list;
}
