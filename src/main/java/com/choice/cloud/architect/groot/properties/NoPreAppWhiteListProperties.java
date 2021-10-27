package com.choice.cloud.architect.groot.properties;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * <p>
 * 没有预发环境的应用白名单
 * </p>
 *
 * @author LZ
 * @date Created in 2020/9/3 16:49
 */
@Getter
@Setter
@ToString
@EnableConfigurationProperties(NoPreAppWhiteListProperties.class)
@ConfigurationProperties(NoPreAppWhiteListProperties.NO_PRE_APP_WHITE_LIST_PROPERTIES_PREFIX)
public class NoPreAppWhiteListProperties {
    public static final String NO_PRE_APP_WHITE_LIST_PROPERTIES_PREFIX = "nopre.app.white";

    private List<String> list;
}
