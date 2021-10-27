package com.choice.cloud.architect.groot.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * <p>
 * Apollo 服务端线下和线上用户名和密码
 * </p>
 *
 * @author LZ
 * @date Created in 2020/5/26 21:39
 */
@Getter
@Component
public class ApolloServerProperties {
    @Value("${apollo.server.offline.username}")
    private String offlineUsername;
    @Value("${apollo.server.offline.password}")
    private String offlinePassword;
    @Value("${apollo.server.online.username}")
    private String onlineUsername;
    @Value("${apollo.server.online.password}")
    private String onlinePassword;
}
