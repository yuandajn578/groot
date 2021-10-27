package com.choice.cloud.architect.groot.service.inner.impl;

import com.choice.cloud.architect.groot.dto.ListIdcInfoDTO;
import com.choice.cloud.architect.groot.properties.IdcLdcOrchestrationProperties;
import com.choice.cloud.architect.groot.service.inner.IdcPublishService;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author zhangkun
 */
@Service
@Slf4j
public class IdcPublishServiceImpl implements IdcPublishService {
    @Autowired
    private IdcLdcOrchestrationProperties idcLdcOrchestrationProperties;

    @Override
    public Set<ListIdcInfoDTO> listIdcInfo() {
        Set<String> idcSet = Sets.newHashSet();
        idcSet.addAll(idcLdcOrchestrationProperties.getOnlineIdc());
        idcSet.addAll(idcLdcOrchestrationProperties.getOfflineIdc());

        return idcSet.stream()
                .map(ListIdcInfoDTO::new)
                .collect(Collectors.toSet());
    }
}
