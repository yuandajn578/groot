package com.choice.cloud.architect.groot;

import com.choice.cloud.architect.groot.websocket.ContainerLogServerEndpoint;
import com.choice.cloud.architect.groot.websocket.ExecServerEndpoint;
import com.choice.driver.ali.mq.annotation.EnableAliMQ;
import com.choice.driver.jwt.annotation.EnableFeignJwt;
import com.choice.driver.shutdown.twox.annotation.EnableGraceShutdown;
import com.spring4all.swagger.EnableSwagger2Doc;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@EnableGraceShutdown
@EnableFeignJwt(enableArgumentResolver = true)
@EnableAspectJAutoProxy
@EnableTransactionManagement
@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
@EnableSwagger2Doc
@MapperScan("com.choice.cloud.architect.groot.dao")
@EnableAliMQ
@EnableWebSocket
public class GRootApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(GRootApplication.class, args);
        ContainerLogServerEndpoint.setApplicationContext(run);
        ExecServerEndpoint.setApplicationContext(run);
    }

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
