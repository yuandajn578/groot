package com.choice.cloud.architect.groot.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @classDesc: 异步线程池配置
 * @Author: yuxiaopeng
 * @createTime: Created in 2018/10/23
 * @version: v1.0
 * @copyright: 北京辰森
 * @email: yuxiaopeng@choicesoft.com.cn
 */
@Slf4j
@EnableAsync
@Configuration
public class AsyncExecutorConfig {

    public static final String ALARM_NOTICE_TASK_EXECUTOR_NAME = "GRootTaskExecutor";

    /**
     * Spring Thread Pool Executor
     * @return
     */
    @Bean
    public ThreadPoolTaskExecutor alarmNoticeTaskExecutor(){
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        availableProcessors = (availableProcessors*2+1) < 5 ? 5: (availableProcessors*2+1);
        final int corePoolSize = availableProcessors;
        final int maxPoolSize = availableProcessors *2;
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        //线程池维护线程的最少数量
        taskExecutor.setCorePoolSize(corePoolSize);
        //允许的空闲时间
        taskExecutor.setKeepAliveSeconds(500);
        //线程池维护线程的最大数量
        taskExecutor.setMaxPoolSize(maxPoolSize);
        //缓存队列
        taskExecutor.setQueueCapacity(1000);
        taskExecutor.setThreadNamePrefix("AlarmNoticeExecutor-");
        //对拒绝task的处理策略
        log.debug("Load Alarm Notice Task Executor Success");
        return taskExecutor;
    }
}
