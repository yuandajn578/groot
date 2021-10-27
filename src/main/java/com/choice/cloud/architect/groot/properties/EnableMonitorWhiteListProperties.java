package com.choice.cloud.architect.groot.properties;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * 允许开启 adminconsole & pinpoint 链路的应用白名单.
 * <p>
 * 服务之间用 ',' 分隔.
 *
 * @author liukai
 */
@Slf4j
@Data
@EnableConfigurationProperties(EnableMonitorWhiteListProperties.class)
@ConfigurationProperties(EnableMonitorWhiteListProperties.PREFIX)
public class EnableMonitorWhiteListProperties {

    public static final String PREFIX = "app.white";

    private String whiteList;
}
