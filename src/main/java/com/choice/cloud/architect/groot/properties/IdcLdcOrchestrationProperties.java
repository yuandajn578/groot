package com.choice.cloud.architect.groot.properties;

import com.alibaba.fastjson.JSON;
import com.choice.cloud.architect.groot.constants.EnvConstants;
import com.choice.cloud.architect.groot.dto.ListIdcLdcDTO;
import com.choice.cloud.architect.groot.support.IdcLdcHolder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author zhangkun
 */
@Slf4j
@EnableConfigurationProperties(IdcLdcOrchestrationProperties.class)
@ConfigurationProperties(IdcLdcOrchestrationProperties.PREFIX)
public class IdcLdcOrchestrationProperties implements InitializingBean {
    public static final String PREFIX = "idc.ldc.orchestration";

    private String detail;
    private List<String> offlineIdc = Lists.newArrayList();
    private List<String> onlineIdc = Lists.newArrayList();
    private Map<String, List<String>> offlineIdcLdcMap = Maps.newHashMap();
    private Map<String, List<String>> onlineIdcLdcMap = Maps.newHashMap();
    private List<ListIdcLdcDTO> offlineIdcLdcList = Lists.newArrayList();
    private List<ListIdcLdcDTO> onlineIdcLdcList = Lists.newArrayList();
    private Map<String, String> idcLdcOnlineOfflineMap = Maps.newHashMap();

    @Override
    public void afterPropertiesSet() throws Exception {
        List<IdcLdcConfig> idcLdcConfigs = JSON.parseArray(this.getDetail(), IdcLdcConfig.class);

        for (IdcLdcConfig idcLdcConfig : idcLdcConfigs) {
            if (idcLdcConfig.getEnv().equals(EnvConstants.ENV_OFFLINE)) {
                String idc = idcLdcConfig.getIdc();
                this.idcLdcOnlineOfflineMap.put(idc, EnvConstants.ENV_OFFLINE);
                this.offlineIdc.add(idc);
                this.offlineIdcLdcMap.put(idc, idcLdcConfig.getLdc());
                if (CollectionUtils.isNotEmpty(idcLdcConfig.getLdc())) {
                    idcLdcConfig.getLdc().forEach(item -> {
                        this.offlineIdcLdcList.add(ListIdcLdcDTO.builder().idc(idc).ldc(item).env(EnvConstants.ENV_OFFLINE).build());
                        this.idcLdcOnlineOfflineMap.put(item, EnvConstants.ENV_OFFLINE);
                    });
                }
            }

            if (idcLdcConfig.getEnv().equals(EnvConstants.ENV_ONLINE)) {
                String idc = idcLdcConfig.getIdc();
                this.idcLdcOnlineOfflineMap.put(idc, EnvConstants.ENV_ONLINE);
                this.onlineIdc.add(idc);
                this.onlineIdcLdcMap.put(idc, idcLdcConfig.getLdc());
                if (CollectionUtils.isNotEmpty(idcLdcConfig.getLdc())) {
                    idcLdcConfig.getLdc().forEach(item -> {
                        this.onlineIdcLdcList.add(ListIdcLdcDTO.builder().idc(idc).ldc(item).env(EnvConstants.ENV_ONLINE).build());
                        this.idcLdcOnlineOfflineMap.put(item, EnvConstants.ENV_ONLINE);
                    });
                }
            }
        }

        IdcLdcHolder.setOfflineIdc(offlineIdc);
        IdcLdcHolder.setOnlineIdc(onlineIdc);
    }

    @Data
    private static class IdcLdcConfig {
        private String idc;
        private List<String> ldc;
        private String env;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public List<String> getOfflineIdc() {
        return Collections.unmodifiableList(offlineIdc);
    }

    public void setOfflineIdc(List<String> offlineIdc) {
        this.offlineIdc = offlineIdc;
    }

    public List<String> getOnlineIdc() {
        return Collections.unmodifiableList(onlineIdc);
    }

    public void setOnlineIdc(List<String> onlineIdc) {
        this.onlineIdc = onlineIdc;
    }

    public Map<String, List<String>> getOfflineIdcLdcMap() {
        return offlineIdcLdcMap;
    }

    public void setOfflineIdcLdcMap(Map<String, List<String>> offlineIdcLdcMap) {
        this.offlineIdcLdcMap = offlineIdcLdcMap;
    }

    public Map<String, List<String>> getOnlineIdcLdcMap() {
        return onlineIdcLdcMap;
    }

    public void setOnlineIdcLdcMap(Map<String, List<String>> onlineIdcLdcMap) {
        this.onlineIdcLdcMap = onlineIdcLdcMap;
    }

    public List<ListIdcLdcDTO> getOfflineIdcLdcList() {
        return offlineIdcLdcList;
    }

    public void setOfflineIdcLdcList(List<ListIdcLdcDTO> offlineIdcLdcList) {
        this.offlineIdcLdcList = offlineIdcLdcList;
    }

    public List<ListIdcLdcDTO> getOnlineIdcLdcList() {
        return onlineIdcLdcList;
    }

    public void setOnlineIdcLdcList(List<ListIdcLdcDTO> onlineIdcLdcList) {
        this.onlineIdcLdcList = onlineIdcLdcList;
    }

    public Map<String, String> getIdcLdcOnlineOfflineMap() {
        return idcLdcOnlineOfflineMap;
    }

    public void setIdcLdcOnlineOfflineMap(Map<String, String> idcLdcOnlineOfflineMap) {
        this.idcLdcOnlineOfflineMap = idcLdcOnlineOfflineMap;
    }
}
