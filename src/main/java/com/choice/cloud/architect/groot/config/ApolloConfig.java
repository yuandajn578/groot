package com.choice.cloud.architect.groot.config;

import com.choice.cloud.architect.groot.properties.ApolloClientConfig;
import com.choice.cloud.architect.groot.properties.ApolloClientProperties;
import com.choice.cloud.architect.groot.support.ApolloRefresh;
import com.ctrip.framework.apollo.openapi.client.ApolloOpenApiClient;
import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @classDesc:
 * @Author: yuxiaopeng
 * @createTime: Created in 2018/10/23
 * @version: v1.0
 * @copyright: 北京辰森
 * @email: yuxiaopeng@choicesoft.com.cn
 */
@Slf4j
@Configuration
@EnableApolloConfig(value = "application")
@EnableConfigurationProperties(ApolloClientProperties.class)
public class ApolloConfig {

    @Autowired
    private ApolloClientProperties apolloClientProperties;
    /**
     * Apollo刷新配置
     * @return
     */
    @Bean
    public ApolloRefresh apolloRefresh(ContextRefresher contextRefresher){
        ApolloRefresh apolloRefresh = new ApolloRefresh(contextRefresher);
        log.debug("Load ApolloRefresh Success");
        return apolloRefresh;
    }

    /**
     * 阿波罗 client 线下
     * @return
     */
    @Bean("apolloOpenApiClientOffline")
    public ApolloOpenApiClient apolloOpenApiClientOffline(){
        return ApolloOpenApiClient.newBuilder()
                .withPortalUrl(apolloClientProperties.getConfig().get("offline").getPortalUrl())
                .withToken(apolloClientProperties.getConfig().get("offline").getToken())
                .build();
    }

    /**
     * 阿波罗 client 线上
     * @return
     */
    @Bean("apolloOpenApiClientOnline")
    public ApolloOpenApiClient apolloOpenApiClientOnline(){
        return ApolloOpenApiClient.newBuilder()
                .withPortalUrl(apolloClientProperties.getConfig().get("online").getPortalUrl())
                .withToken(apolloClientProperties.getConfig().get("online").getToken())
                .build();
    }
}
