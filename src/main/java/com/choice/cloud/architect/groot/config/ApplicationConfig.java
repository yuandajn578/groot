package com.choice.cloud.architect.groot.config;

import com.aliyun.openservices.ons.api.bean.ConsumerBean;
import com.choice.cloud.architect.groot.properties.*;
import com.choice.cloud.architect.groot.support.ApplicationShutdownJob;
import com.choice.driver.shutdown.twox.core.ShutdownJob;
import feign.Logger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;

/**
 * @classDesc:
 * @author: yuxiaopeng
 * @createTime: 2020-02-22
 * @version: v1.0.0
 * @email: yuxiaopeng@choicesoft.com.cn
 */
@Slf4j
@Configuration
public class ApplicationConfig {

    @Bean
    public Logger.Level getFeignLoggerLevel(ApplicationProperties applicationProperties){
        Logger.Level level;
        if(log.isDebugEnabled() && applicationProperties.getEnableFeignLog()){
            level = Logger.Level.FULL;
        } else {
            level = Logger.Level.BASIC;
        }
        log.info("Load Feign Logger Level Success,Level:{} ",level);
        return level;
    }

    @RefreshScope
    @Bean
    public ApplicationProperties applicationProperties(){
        ApplicationProperties applicationProperties =  new ApplicationProperties();
        log.debug("Load ApplicationProperties Success");
        return applicationProperties;
    }

    @RefreshScope
    @Bean
    public WhiteListProperties whiteListProperties(){
        WhiteListProperties whiteListProperties =  new WhiteListProperties();
        log.debug("Load WhiteListProperties Success");
        return whiteListProperties;
    }

    @RefreshScope
    @Bean
    public NoPreAppWhiteListProperties noPreAppWhiteListProperties(){
        NoPreAppWhiteListProperties noPreAppWhiteListProperties =  new NoPreAppWhiteListProperties();
        log.debug("Load NoPreAppWhiteListProperties Success");
        return noPreAppWhiteListProperties;
    }

    @Bean
    public ShutdownJob shutdownJob(List<ConsumerBean> consumerBeanList, ThreadPoolTaskExecutor alarmNoticeTaskExecutor){
        ShutdownJob shutdownJob = new ApplicationShutdownJob(consumerBeanList, alarmNoticeTaskExecutor);
        log.debug("Load Application Shutdown Job Success");
        return shutdownJob;
    }

    @RefreshScope
    @Bean
    public KubernetesConfigProperties kubernetesConfigProperties() {
        KubernetesConfigProperties kubernetesConfigProperties = new KubernetesConfigProperties();
        log.debug("Load KubernetesConfigProperties Success");
        return kubernetesConfigProperties;
    }

    @RefreshScope
    @Bean
    public IdcLdcOrchestrationProperties idcLdcOrchestrationProperties() {
        IdcLdcOrchestrationProperties idcLdcOrchestrationProperties = new IdcLdcOrchestrationProperties();
        log.debug("Load IdcLdcOrchestrationProperties Success");
        return idcLdcOrchestrationProperties;
    }

    @RefreshScope
    @Bean
    public PodOperatorWhiteListProperties podOperatorWhiteListProperties() {
        PodOperatorWhiteListProperties podOperatorWhiteListProperties = new PodOperatorWhiteListProperties();
        log.debug("Load PodOperatorWhiteListProperties Success");
        return podOperatorWhiteListProperties;
    }

    @RefreshScope
    @Bean
    public EnableMonitorWhiteListProperties enableMonitorWhiteListProperties() {
        EnableMonitorWhiteListProperties  enableMonitorWhiteListProperties = new EnableMonitorWhiteListProperties();
        log.debug("Load EnableMonitorWhiteListProperties Success");
        return enableMonitorWhiteListProperties;
    }
}
