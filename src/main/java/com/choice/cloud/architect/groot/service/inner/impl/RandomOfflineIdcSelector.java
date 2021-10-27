package com.choice.cloud.architect.groot.service.inner.impl;

import com.choice.cloud.architect.groot.properties.IdcLdcOrchestrationProperties;
import com.choice.cloud.architect.groot.service.inner.IdcSelector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

/**
 * @author zhangkun
 */
@Service
public class RandomOfflineIdcSelector implements IdcSelector {
    @Autowired
    private IdcLdcOrchestrationProperties idcLdcOrchestrationProperties;

    @Override
    public String select() {
        List<String> offlineIdc = idcLdcOrchestrationProperties.getOfflineIdc();
        if (offlineIdc.isEmpty()) {
            throw new RuntimeException("线下无IDC配置");
        }

        int offlineIdcCount = offlineIdc.size();
        if (offlineIdcCount == 1) {
            return offlineIdc.get(0);
        }

        Random random = new Random();
        int index = random.nextInt(offlineIdcCount);
        return offlineIdc.get(index);
    }
}
