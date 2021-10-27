package com.choice.cloud.architect.groot.service.inner.impl;

import com.alibaba.fastjson.JSON;
import com.choice.cloud.architect.groot.dto.apollo.ApolloBasicDTO;
import com.choice.cloud.architect.groot.dto.apollo.ApolloCompareRequestDTO;
import com.choice.cloud.architect.groot.exception.ServiceException;
import com.choice.cloud.architect.groot.properties.ApolloClientConfig;
import com.choice.cloud.architect.groot.properties.ApolloClientProperties;
import com.choice.cloud.architect.groot.response.code.ApolloResponseCode;
import com.choice.cloud.architect.groot.service.inner.ApolloClientService;
import com.ctrip.framework.apollo.openapi.client.ApolloOpenApiClient;
import com.ctrip.framework.apollo.openapi.dto.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ApolloClientServiceImpl implements ApolloClientService {

    @Autowired
    @Qualifier("apolloOpenApiClientOffline")
    private ApolloOpenApiClient apolloOpenApiClientOffline;

    @Autowired
    @Qualifier("apolloOpenApiClientOnline")
    private ApolloOpenApiClient apolloOpenApiClientOnline;

    @Autowired
    private ApolloClientProperties apolloClientProperties;

    public ApolloOpenApiClient getClientByEnv(String env){
        Map<String, ApolloClientConfig> config = apolloClientProperties.getConfig();
        Set<Map.Entry<String, ApolloClientConfig>> entries = config.entrySet();
        for (Map.Entry<String, ApolloClientConfig> entry: entries) {
            if(entry.getValue().getEnvs().contains(env.toUpperCase())){
                return entry.getKey().equalsIgnoreCase("online")?apolloOpenApiClientOnline:apolloOpenApiClientOffline;
            }
        }
        throw new ServiceException("apollo env Invalid ！！！");
    }

    @Override
    public List<OpenEnvClusterDTO> getAllEnvClusters(String appId) {
        List<OpenEnvClusterDTO> envClusterInfoOffline = apolloOpenApiClientOffline.getEnvClusterInfo(appId);
        //List<OpenEnvClusterDTO> envClusterInfoOnline = apolloOpenApiClientOnline.getEnvClusterInfo(appId);
        //envClusterInfoOffline.addAll(envClusterInfoOnline);
        return envClusterInfoOffline;
    }

    @Override
    public List<OpenNamespaceDTO> getAllClusterNamespaces(String appId, String env, String clusterName) {
        return getClientByEnv(env).getNamespaces(appId,env,clusterName);
    }

    @Override
    public OpenNamespaceDTO getClusterAllNamespaces(String appId, String env, String clusterName, String namespaceName) {
        try {
            return getClientByEnv(env).getNamespace(appId,env,clusterName,namespaceName);
        } catch (Exception e) {
            throw new ServiceException(ApolloResponseCode.NULL_NAMESPACE);
        }

    }

    @Override
    public OpenAppNamespaceDTO createAppNamespace(OpenAppNamespaceDTO appNamespaceDTO) {
        return apolloOpenApiClientOffline.createAppNamespace(appNamespaceDTO);
    }

    @Override
    public OpenNamespaceLockDTO getNamespaceLock(String appId, String env, String clusterName, String namespaceName) {
        return getClientByEnv(env).getNamespaceLock(appId,env,clusterName,namespaceName);
    }

    @Override
    public OpenItemDTO createItem(String appId, String env, String clusterName, String namespaceName, OpenItemDTO itemDTO) {
        return getClientByEnv(env).createItem(appId,env,clusterName,namespaceName,itemDTO);
    }

    @Override
    public void updateItem(String appId, String env, String clusterName, String namespaceName, OpenItemDTO itemDTO) {
        getClientByEnv(env).updateItem(appId,env,clusterName,namespaceName,itemDTO);
    }

    @Override
    public void createOrUpdateItem(String appId, String env, String clusterName, String namespaceName, OpenItemDTO itemDTO) {
        getClientByEnv(env).createOrUpdateItem(appId,env,clusterName,namespaceName,itemDTO);
    }

    @Override
    public void removeItem(String appId, String env, String clusterName, String namespaceName, String key, String operator) {
        getClientByEnv(env).removeItem(appId,env,clusterName,namespaceName,key,operator);
    }

    @Override
    public OpenReleaseDTO publishNamespace(String appId, String env, String clusterName, String namespaceName, NamespaceReleaseDTO releaseDTO) {
        return getClientByEnv(env).publishNamespace(appId,env,clusterName,namespaceName,releaseDTO);
    }

    @Override
    public OpenReleaseDTO getLatestActiveRelease(String appId, String env, String clusterName, String namespaceName) {
        return getClientByEnv(env).getLatestActiveRelease(appId,env,clusterName,namespaceName);
    }

    /**
     * 根据key获取配置项value
     *
     * @param appId
     * @param env
     * @param cluster
     * @param key
     * @return
     */
    @Override
    public OpenItemDTO getOpenItemByKey(String appId, String env, String cluster, String key) {
        OpenNamespaceDTO application = this.getClusterAllNamespaces(appId, env, cluster, "application");
        Optional.ofNullable(application).orElseThrow(() -> new ServiceException("阿波罗集群不存在"));

        List<OpenItemDTO> itemsList = application.getItems();
        Map<String, List<OpenItemDTO>> itemsMap = itemsList.stream().collect(Collectors.groupingBy(OpenItemDTO::getKey));

        return itemsMap.get(key).get(0);
    }

    /**
     * 获取所有配置项
     *
     * @param appId
     * @param env
     * @param cluster
     * @return
     */
    @Override
    public List<OpenItemDTO> getAllOpenItem(String appId, String env, String cluster) {
        OpenNamespaceDTO application = this.getClusterAllNamespaces(appId, env, cluster, "application");
        Optional.ofNullable(application).orElseThrow(() -> new ServiceException("阿波罗集群不存在"));

        List<OpenItemDTO> itemsList = application.getItems();
        itemsList = itemsList.stream().filter(item -> StringUtils.isNotBlank(item.getKey())).collect(Collectors.toList());
        return Optional.ofNullable(itemsList).orElseThrow(() -> new ServiceException("阿波罗集群下没有查询到对应的配置项"));
    }

    /**
     * 对比配置
     * @param compareRequest
     */
    @Override
    public List<String> compareCluster(ApolloCompareRequestDTO compareRequest) {
        if (compareRequest == null) {
            return null;
        }
        Map<String, String> sourceItemMap = this.findAllItem(compareRequest.getSource());
        Map<String, String> targetItemMap = this.findAllItem(compareRequest.getTarget());
        return compareItem(sourceItemMap, targetItemMap);
    }

    public List<String> compareItem(Map<String, String> sourceItemMap, Map<String, String> targetItemMap) {
        List<String> sameList = Lists.newArrayList();
        List<String> noSameList = Lists.newArrayList();
        List<String> noSourceList = Lists.newArrayList();

        for (String key : sourceItemMap.keySet()) {
            String sourceValue = sourceItemMap.get(key) == null ? "" : sourceItemMap.get(key);
            String targetValue = targetItemMap.get(key) == null ? "" : targetItemMap.get(key);
            if (sourceValue.equals(targetValue)) {
                sameList.add("相同，key:" + key + "，value:" + sourceValue);
            } else {
                noSameList.add("不同，key:" + key + "，sourceValue:" + sourceValue + "，targetValue:" + targetValue);
            }
        }
        for (String key : targetItemMap.keySet()) {
            if (!sourceItemMap.containsKey(key)) {
                noSourceList.add("source中没有的key:" + key + "，value:" + targetItemMap.get(key));
            }
        }
        List<String> retList = Lists.newArrayList();
        retList.addAll(sameList);
        retList.addAll(noSameList);
        retList.addAll(noSourceList);
        return retList;
    }

    public Map<String, String> findAllItem(ApolloBasicDTO basicDTO) {
        List<OpenNamespaceDTO> temp = this.getAllClusterNamespaces(basicDTO.getAppId(), basicDTO.getEnv(), basicDTO.getClusterName());

        Map<String, String> ret = Maps.newHashMap();
        for (int index = basicDTO.getNamespacesList().size() -1; index >= 0; index--) {
            String namespaces = basicDTO.getNamespacesList().get(index);
            if (!"application".equals(namespaces)) { // 公共配置
                OpenNamespaceDTO commonNamespaceRet = this.getClusterAllNamespaces("pay-server-shared", basicDTO.getEnv(), basicDTO.getClusterName(), namespaces);
                for (OpenItemDTO item : commonNamespaceRet.getItems()) {
                    ret.put(item.getKey(), item.getValue());
                }
            }
            OpenNamespaceDTO namespaceRet = this.getClusterAllNamespaces(basicDTO.getAppId(), basicDTO.getEnv(), basicDTO.getClusterName(), namespaces);
            for (OpenItemDTO item : namespaceRet.getItems()) {
                ret.put(item.getKey(), item.getValue());
            }
        }
        return ret;
    }
}
