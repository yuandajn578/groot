package com.choice.cloud.architect.groot.eureka;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.choice.cloud.architect.groot.enums.EnvOnOffEnum;
import com.choice.cloud.architect.groot.enums.EurekaStatusEnum;
import com.choice.cloud.architect.groot.eureka.dto.EurekaConfig;
import com.choice.cloud.architect.groot.eureka.dto.EurekaDTO;
import com.choice.cloud.architect.groot.eureka.dto.InstanceDTO;
import com.choice.cloud.architect.groot.eureka.dto.ServiceGroupInstanceDTO;
import com.choice.cloud.architect.groot.exception.ServiceException;
import com.choice.cloud.architect.groot.response.code.ChangeResponseCode;
import com.choice.cloud.architect.groot.response.code.EurekaResponseCode;
import com.choice.cloud.architect.groot.response.code.SysResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.*;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;

/**
 * @ClassName ShutdownEurekaService
 * @Description 强制注册到eureka上的服务下线
 * @Author Guangshan Wang
 * @Date 2020/5/15/015 14:14
 */
@Service
@Slf4j
public class ShutdownEurekaService {

    @Autowired
    private EurekaService eurekaService;

    private RestTemplate restTemplate = new RestTemplate();

    @Resource
    private EurekaConfig eurekaConfig;



    /**
     * 下掉某个实例
     * @param serviceId
     * @param instanceId
     * @return
     */
    public boolean updateInstance(String serviceId, String instanceId, EurekaStatusEnum eurekaStatusEnum) throws Exception {
        EurekaDTO eurekaDTO = eurekaService.getEurekaInfo(EnvOnOffEnum.offline);
        String url = eurekaDTO.getUrl() + "/" + serviceId + "/" + instanceId + "/status?value=" + eurekaStatusEnum.getCode();
        String userMsg = eurekaDTO.getUser() + ":" + eurekaDTO.getPassword();
        String base64UserMsg = Base64.getEncoder().encodeToString(userMsg.getBytes());
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPut httpPut = new HttpPut(url);
        httpPut.addHeader("Authorization", "Basic " + base64UserMsg);
        CloseableHttpResponse response = httpClient.execute(httpPut);
        int code = response.getStatusLine().getStatusCode();
        log.info("updateInstance status {}, serviceId {}, instanceId {}, code {}", eurekaStatusEnum, serviceId, instanceId, code);
        if (code == HTTP_OK) {
            return true;
        } else if (code == HTTP_NOT_FOUND) {
            throw new ServiceException(EurekaResponseCode.NO_FOUND_INSTANCE);
        } else {
            throw new ServiceException(SysResponseCode.ERROR);
        }
    }

    /**
     * 关闭所有 没有服务分组的服务
     */
    public void shutdownAllInstanceNoServiceGroup() throws Exception {
        // 查询没有服务分组的实例
        List<InstanceDTO> noServiceGroupInstanceList = this.queryInstanceNoServiceGroup();
        if (noServiceGroupInstanceList == null) {
            return ;
        }
        // 过滤白名单
        List<InstanceDTO> instanceList = this.serviceWhiteList(noServiceGroupInstanceList);

        for (InstanceDTO instance : instanceList) {
            log.info("需要关闭的实例 serviceId {},instanceId {},", instance.getServiceId(), instance.getInstanceId());
            this.updateInstance(instance.getServiceId(), instance.getInstanceId(), EurekaStatusEnum.OUT_OFF_SERVICE);
        }
    }

    /**
     * 过滤白名单
     * @param instanceList
     * @return
     */
    private List<InstanceDTO> serviceWhiteList(List<InstanceDTO> instanceList) {
        List<InstanceDTO> retInstanceList = Lists.newArrayList();
        if (instanceList == null) {
            return retInstanceList;
        }
        if (StringUtils.isEmpty(eurekaConfig.getServiceWhiteList())) {
            return retInstanceList;
        }
        List<String> whiteList = Arrays.asList(eurekaConfig.getServiceWhiteList().split(","));

        for (InstanceDTO instance : instanceList) {
            if (!whiteList.contains(instance.getServiceId())) {
                retInstanceList.add(instance);
            }
        }
        return retInstanceList;
    }


    /**
     * 查询没有设置服务分组的实例
     * @return
     */
    public List<InstanceDTO> queryInstanceNoServiceGroup() {
        List<InstanceDTO> retInstanceList = Lists.newArrayList();
        List<ServiceGroupInstanceDTO> allService = eurekaService.getAllService(EnvOnOffEnum.offline);
        if (allService == null) {
            return retInstanceList;
        }
        for (ServiceGroupInstanceDTO serviceGroupInstanceDTO : allService) {
            List<InstanceDTO> instanceList = serviceGroupInstanceDTO.getInstanceList();
            if (instanceList == null) {
                continue;
            }
            for (InstanceDTO instanceDTO : instanceList) {
                if (instanceDTO.getServerGroup() == null) {
                    retInstanceList.add(instanceDTO);
                }
            }
        }
        return retInstanceList;
    }

}
