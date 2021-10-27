package com.choice.cloud.architect.groot.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Author: zhangguoquan
 * @Date: 2020/5/25 13:54
 */
@Data
@Component
public class EurekaServerProperties {

    @Value("${eureka.server.offline.username}")
    private String offlineUsername;

    @Value("${eureka.server.offline.password}")
    private String offlinePassword;

    @Value("${eureka.server.offline.apps.url}")
    private String offlineAppsUrl;

    @Value("${eureka.server.online.username}")
    private String onlineUsername;

    @Value("${eureka.server.online.password}")
    private String onlinePassword;

    @Value("${eureka.server.online.apps.url}")
    private String onlineAppsUrl;
}
