package com.choice.cloud.architect.groot.config;

import com.alibaba.fastjson.JSON;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author zhangkun
 */
@Slf4j
@Configuration
public class KubernetesClientConfig {
    @Bean
    public KubernetesClient offlineKubernetesClient() throws IOException {
        return create("kubeConfig-offline.yml");
    }

    @Bean
    public KubernetesClient onlineKubernetesClient() throws IOException {
        return create("kubeConfig-online.yml");
    }

    @Bean
    public KubernetesClient onlineKubernetesClient2() throws IOException {
        return create("kubeConfig-online-2.yml");
    }

    private KubernetesClient create(String configFile) throws IOException {
        InputStream inputStream = KubernetesClientConfig.class.getClassLoader().getResourceAsStream(configFile);
        ;

        if (null == inputStream) {
            throw new RuntimeException("Init DefaultKubernetesClient Error");
        }

        String kubeconfigContents = IOUtils.toString(inputStream);
        Config config = Config.fromKubeconfig(kubeconfigContents);

        config.setTrustCerts(false);
        KubernetesClient client = new DefaultKubernetesClient(config);
        //log.info("创建k8s客户端 client={}", JSON.toJSONString(client));
        return client;
    }
}
