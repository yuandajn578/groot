
package com.choice.cloud.architect.groot.eureka;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.choice.cloud.architect.groot.enums.EnvOnOffEnum;
import com.choice.cloud.architect.groot.eureka.dto.EurekaDTO;
import com.choice.cloud.architect.groot.eureka.dto.InstanceDTO;
import com.choice.cloud.architect.groot.eureka.dto.ServiceGroupInstanceDTO;
import com.choice.cloud.architect.groot.properties.EurekaServerProperties;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;


/**
 * @ClassName EurekaService
 * @Description TODO
 * @Author Guangshan Wang
 * @Date 2020/4/26/026 15:31
 */
@Service
@Slf4j
public class EurekaService {

    private RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private EurekaServerProperties eurekaServerProperties;

    /**
     * 获取所有服务列表
     * @param envOnOffEnum
     * @return
     */
    public List<ServiceGroupInstanceDTO> getAllService(EnvOnOffEnum envOnOffEnum) {
        JSONArray applicationJsonArray = null;
        EurekaDTO eurekaDTO = this.getEurekaInfo(envOnOffEnum);
        applicationJsonArray = this.getAllServiceFromEureka(eurekaDTO);
        return this.formatInstanceList(applicationJsonArray);
    }

    /**
     * 获取eureka信息 包括账号/密码/url
     * @param envOnOffEnum
     * @return
     */
    public EurekaDTO getEurekaInfo(EnvOnOffEnum envOnOffEnum) {
        String user = eurekaServerProperties.getOfflineUsername();
        String password = eurekaServerProperties.getOfflinePassword();
        String url = eurekaServerProperties.getOfflineAppsUrl();
        if (EnvOnOffEnum.online.equals(envOnOffEnum)) {
            user = eurekaServerProperties.getOnlineUsername();
            password = eurekaServerProperties.getOnlinePassword();
            url = eurekaServerProperties.getOnlineAppsUrl();
        }
        return new EurekaDTO(user, password, url);
    }

    /**
     * 去eureka获取所有服务信息
     * @return
     */
    private JSONArray getAllServiceFromEureka(EurekaDTO eurekaDTO) {
        if (eurekaDTO == null) {
            return null;
        }
        String userMsg = eurekaDTO.getUser() + ":" + eurekaDTO.getPassword();
        String base64UserMsg = Base64.getEncoder().encodeToString(userMsg.getBytes());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64UserMsg);
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
        ResponseEntity<JSONObject> resEntity = restTemplate.exchange(eurekaDTO.getUrl().toString(), HttpMethod.GET, requestEntity, JSONObject.class);
        JSONObject responseBody = resEntity.getBody();
        JSONArray applicationJsonArray = responseBody.getJSONObject("applications").getJSONArray("application");
        return applicationJsonArray;
    }

    /**
     * 由json格式转为java格式
     * @param applicationJsonArray
     * @return
     */
    private List<ServiceGroupInstanceDTO> formatInstanceList(JSONArray applicationJsonArray) {
        if (applicationJsonArray == null) {
            return null;
        }
        List<ServiceGroupInstanceDTO> retList = Lists.newArrayList();
        for (int index = 0; index < applicationJsonArray.size(); index++) {
            JSONObject applicationJsonObject = applicationJsonArray.getJSONObject(index);
            JSONArray instanceJsonArray = applicationJsonObject.getJSONArray("instance");

            Map<String, List<InstanceDTO>> serverGroupMap = Maps.newHashMap();
            for (int insIndex = 0; insIndex < instanceJsonArray.size(); insIndex++) {
                JSONObject instanceJsonObject = instanceJsonArray.getJSONObject(insIndex);
                InstanceDTO instanceDTO = this.format(instanceJsonObject);

                List<InstanceDTO> instanceDTOListByGroup = serverGroupMap.get(instanceDTO.getServerGroup());
                if (instanceDTOListByGroup == null) {
                    instanceDTOListByGroup = Lists.newArrayList(instanceDTO);
                    serverGroupMap.put(instanceDTO.getServerGroup(), instanceDTOListByGroup);
                } else {
                    instanceDTOListByGroup.add(instanceDTO);
                }
            }
            for (String serverGroupName : serverGroupMap.keySet()) {
                List<InstanceDTO> instanceDTOList = serverGroupMap.get(serverGroupName);
                ServiceGroupInstanceDTO serviceGroupInstanceDTO = new ServiceGroupInstanceDTO();
                serviceGroupInstanceDTO.setServiceId(instanceDTOList.get(0).getServiceId());
                serviceGroupInstanceDTO.setServiceGroup(instanceDTOList.get(0).getServerGroup());
                serviceGroupInstanceDTO.setInstanceList(instanceDTOList);
                retList.add(serviceGroupInstanceDTO);
            }
        }

        // 服务名正序
        retList.sort(Comparator.comparing(ServiceGroupInstanceDTO::getServiceId));
        return retList;
    }

    /**
     * instance json格式转java bean
     * @param instance
     * @return
     */
    private InstanceDTO format(JSONObject instance) {
        if (instance == null) {
            return null;
        }
        InstanceDTO dto = new InstanceDTO();
        dto.setInstanceId(instance.getString("instanceId"));
        dto.setHost(instance.getString("ipAddr"));
        dto.setUri(instance.getString("homePageUrl"));
        dto.setServiceId(instance.getString("app"));
        dto.setStatus(instance.getString("status"));
        dto.setServerGroup(instance.getJSONObject("metadata").getString("service.group"));

        dto.setRegistrationTimestamp(LocalDateTime.ofEpochSecond(instance.getJSONObject("leaseInfo").getLong("registrationTimestamp")/1000,0, ZoneOffset.ofHours(8)));
        dto.setRenewalTimestamp(LocalDateTime.ofEpochSecond(instance.getJSONObject("leaseInfo").getLong("lastRenewalTimestamp")/1000,0, ZoneOffset.ofHours(8)));
        dto.setServiceUpTimestamp(LocalDateTime.ofEpochSecond(instance.getJSONObject("leaseInfo").getLong("serviceUpTimestamp")/1000,0, ZoneOffset.ofHours(8)));
        return dto;
    }

    /**
     * 按照服务分组查询
     * @param serverGroup
     * @return
     */
    public List<ServiceGroupInstanceDTO> findServiceByGroup(String serverGroup, EnvOnOffEnum envOnOffEnum) {
        List<ServiceGroupInstanceDTO> retList = Lists.newArrayList();
        List<ServiceGroupInstanceDTO> allServiceGroupList = this.getAllService(envOnOffEnum);
        for (ServiceGroupInstanceDTO serviceGroupDTO : allServiceGroupList) {
            if (serverGroup == null && serviceGroupDTO.getServiceGroup() == null) {
                retList.add(serviceGroupDTO);
            } else if (serverGroup != null && serverGroup.equals(serviceGroupDTO.getServiceGroup())){
                retList.add(serviceGroupDTO);
            }
        }
        return retList;
    }

    /**
     * 统计eureka服务数量
     */
    public void serverCount(EnvOnOffEnum envOnOffEnum) {
        List<ServiceGroupInstanceDTO> offlineServiceGroupList = this.getAllService(envOnOffEnum);
        Map<String, List<InstanceDTO>> ret = new HashMap<>();
        for (ServiceGroupInstanceDTO serviceGroupInstanceDTO : offlineServiceGroupList) {
            List<InstanceDTO> instanceDTOList = ret.get(serviceGroupInstanceDTO.getServiceId());
            if (instanceDTOList == null) {
                ret.put(serviceGroupInstanceDTO.getServiceId(), serviceGroupInstanceDTO.getInstanceList());
            } else {
                instanceDTOList.addAll(serviceGroupInstanceDTO.getInstanceList());
            }
        }
        int allCount = 0;
        for (String key : ret.keySet()) {
            int count = 0;
            for (InstanceDTO instanceDTO : ret.get(key)) {
                if (instanceDTO.getHost().startsWith("20.") || instanceDTO.getHost().startsWith("11.")) {
                    count++;
                    allCount++;
                }
            }
            if (count != 0) {
                System.out.println("服务数量：" + count + ", 服务名称：" + key);
            }
        }
        System.out.println("总服务数量：" + allCount);
    }
}
