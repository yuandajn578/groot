package com.choice.cloud.architect.groot.support;

import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.refresh.ContextRefresher;

import java.util.Set;

/**
 * @classDesc: Apollo刷新配置
 * @Author: yuxiaopeng
 * @createTime: Created in 2018/10/23
 * @version: v1.0
 * @copyright: 北京辰森
 * @email: yuxiaopeng@choicesoft.com.cn
 */
@Slf4j
public class ApolloRefresh {

    private ContextRefresher contextRefresher;

    public ApolloRefresh(ContextRefresher contextRefresher) {
        this.contextRefresher = contextRefresher;
    }

    @ApolloConfigChangeListener("application")
    public void applicationOnChange(ConfigChangeEvent changeEvent) {
        Set<String> changedKeys = changeEvent.changedKeys();
        if(!changedKeys.isEmpty()){
            log.debug("[ApolloRefresh]Receive Refresh Keys :{}",changedKeys);
            contextRefresher.refresh();
        }
    }

}
