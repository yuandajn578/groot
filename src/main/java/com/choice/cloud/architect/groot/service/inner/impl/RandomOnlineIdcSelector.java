package com.choice.cloud.architect.groot.service.inner.impl;

import com.choice.cloud.architect.groot.properties.IdcLdcOrchestrationProperties;
import com.choice.cloud.architect.groot.service.inner.IdcSelector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhangkun
 */
@Service
public class RandomOnlineIdcSelector implements IdcSelector {
    @Autowired
    private IdcLdcOrchestrationProperties idcLdcOrchestrationProperties;

    private AtomicInteger atomicInteger = new AtomicInteger();

    @Override
    public String select() {
        List<String> onlineIdc = idcLdcOrchestrationProperties.getOnlineIdc();
        if (onlineIdc.isEmpty()) {
            throw new RuntimeException("线上无IDC配置");
        }

        int onlineIdcCount = onlineIdc.size();
        if (onlineIdcCount == 1) {
            return onlineIdc.get(0);
        }

        int index = atomicInteger.getAndIncrement() / onlineIdc.size();
        return onlineIdc.get(index);
    }
}
