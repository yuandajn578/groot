package com.choice.cloud.architect.groot.support;

import com.aliyun.openservices.ons.api.bean.ConsumerBean;
import com.choice.driver.shutdown.twox.core.ShutdownJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @classDesc:
 * @author: yuxiaopeng
 * @createTime: 2020-02-27
 * @version: v1.0.0
 * @email: yuxiaopeng@choicesoft.com.cn
 */
@Slf4j
public class ApplicationShutdownJob implements ShutdownJob {

    private List<ConsumerBean> consumerBeanList;
    private ThreadPoolTaskExecutor alarmNoticeTaskExecutor;

    public ApplicationShutdownJob(List<ConsumerBean> consumerBeanList, ThreadPoolTaskExecutor alarmNoticeTaskExecutor) {
        this.consumerBeanList = consumerBeanList;
        this.alarmNoticeTaskExecutor = alarmNoticeTaskExecutor;
    }

    @Override
    public void executeBeforeJob() {
        if(CollectionUtils.isNotEmpty(consumerBeanList)){
            for(ConsumerBean consumerBean : consumerBeanList){
                if(null != consumerBean){
                    consumerBean.shutdown();
                    log.debug("[ApplicationShutdownJob] Shutdown Ali MQ Consumer ");
                }
            }
        }
        ThreadPoolExecutor executor = alarmNoticeTaskExecutor.getThreadPoolExecutor();
        if(null != executor){
            try {
                executor.shutdown();
                if(!executor.awaitTermination(50, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
            }
            log.debug("[ApplicationShutdownJob] Shutdown Alarm Notice ThreadPoolTaskExecutor ");
        }
    }

    @Override
    public void executeAfterJob() {

    }
}
