package com.choice.cloud.architect.groot.remote.jenkins;

import com.choice.cloud.architect.groot.exception.ServiceException;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.client.JenkinsHttpClient;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

import java.net.URI;
import java.net.URISyntaxException;

@Component
@Getter
public class JenkinsClientBuilder {

    @Value("${jenkins.openapi.url}")
    private String jenkinsUrl;

    @Value("${jenkins.openapi.username}")
    private String username;

    @Value("${jenkins.openapi.password}")
    private String password;

    @Bean(name = "jenkinsHttpClient")
    @Scope(scopeName = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public JenkinsHttpClient getJenkinsHttpClient() {
        try {
            return new JenkinsHttpClient(new URI(jenkinsUrl), username, password);
        } catch (URISyntaxException e) {
            throw new ServiceException("jenkins 连接失败！");
        }
    }

    @Bean(name = "jenkinsServer")
    @Scope(scopeName = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public JenkinsServer getJenkinsServer(@Qualifier("jenkinsHttpClient") JenkinsHttpClient jenkinsHttpClient) {
        return new JenkinsServer(jenkinsHttpClient);
    }
}
