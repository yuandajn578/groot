package com.choice.cloud.architect.groot.properties;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * <p>
 *     允许操作Pod扩容的人员白名单
 * </p>
 * @author zhangkun
 */
@Slf4j
@Data
@EnableConfigurationProperties(PodOperatorWhiteListProperties.class)
@ConfigurationProperties(PodOperatorWhiteListProperties.PREFIX)
public class PodOperatorWhiteListProperties {
    public static final String PREFIX = "pod.operator";

    private String whiteList;
}
