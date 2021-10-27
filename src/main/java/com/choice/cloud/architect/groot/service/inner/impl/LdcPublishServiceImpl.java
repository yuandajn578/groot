package com.choice.cloud.architect.groot.service.inner.impl;

import com.choice.cloud.architect.groot.dto.ListIdcLdcDTO;
import com.choice.cloud.architect.groot.properties.IdcLdcOrchestrationProperties;
import com.choice.cloud.architect.groot.service.inner.LdcPublishService;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.choice.cloud.architect.groot.enums.EnvTypeEnum.pro;
import static com.choice.cloud.architect.groot.enums.EnvTypeEnum.test;

/**
 * @author zhangkun
 */
@Slf4j
@Service
public class LdcPublishServiceImpl implements LdcPublishService {
    @Autowired
    private IdcLdcOrchestrationProperties idcLdcOrchestrationProperties;

    @Resource(name = "offlineKubernetesClient")
    private KubernetesClient offlineKubernetesClient;

    @Resource(name = "onlineKubernetesClient")
    private KubernetesClient onlineKubernetesClient;

    @Override
    public String randomLdc(String envType, String idc) {
        log.info("LdcPublishService get random ldc, envType = {}, idc = {}", envType, idc);

        List<String> ldcList;
        if (test.value().equals(envType)) {
            Map<String, List<String>> offlineIdcLdcMap =
                    idcLdcOrchestrationProperties.getOfflineIdcLdcMap();
            ldcList = offlineIdcLdcMap.get(idc);
        } else if (pro.value().equals(envType)) {
            Map<String, List<String>> onlineIdcLdcMap = idcLdcOrchestrationProperties.getOnlineIdcLdcMap();
            ldcList = onlineIdcLdcMap.get(idc);
        } else {
            throw new IllegalArgumentException("无此环境类型配置");
        }

        if (CollectionUtils.isEmpty(ldcList)) {
            return null;
        }

        int size = ldcList.size();
        if (size == 1) {
            return ldcList.get(0);
        }

        Random random = new Random();
        int index = random.nextInt(size);

        return ldcList.get(index);
    }

    @Override
    public List<ListIdcLdcDTO> listLdcInfo() {
        List<ListIdcLdcDTO> listLdcDTOS = new ArrayList<ListIdcLdcDTO>();
        List<ListIdcLdcDTO> offlineIdcLdc = idcLdcOrchestrationProperties.getOfflineIdcLdcList();
        List<ListIdcLdcDTO> onlineIdcLdc = idcLdcOrchestrationProperties.getOnlineIdcLdcList();
        listLdcDTOS.addAll(offlineIdcLdc);
        listLdcDTOS.addAll(onlineIdcLdc);
        log.info("listLdcDTOS:{}", listLdcDTOS.toString());
        return listLdcDTOS;
    }
}
