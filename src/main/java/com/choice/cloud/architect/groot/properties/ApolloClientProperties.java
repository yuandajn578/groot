package com.choice.cloud.architect.groot.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Getter
@Setter
@ConfigurationProperties(prefix = "apollo.client")
public class ApolloClientProperties {

    private Map<String,ApolloClientConfig> config;
}
