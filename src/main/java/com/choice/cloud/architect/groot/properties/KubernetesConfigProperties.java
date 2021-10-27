package com.choice.cloud.architect.groot.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * <p>
 * k8s相关配置
 * </p>
 *
 * @author zhangkun
 */
@Getter
@Setter
@ToString
@EnableConfigurationProperties(KubernetesConfigProperties.class)
@ConfigurationProperties(KubernetesConfigProperties.KUBERNETES_CONFIG_PROPERTIES_PREFIX)
public class KubernetesConfigProperties {
    public static final String KUBERNETES_CONFIG_PROPERTIES_PREFIX = "kubernetes.cluster";

    /**
     * k8s集群环境 online/offline
     */
    private String env;
}
