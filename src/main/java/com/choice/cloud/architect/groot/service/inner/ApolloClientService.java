package com.choice.cloud.architect.groot.service.inner;

import java.util.List;
import java.util.Map;

import com.choice.cloud.architect.groot.dto.apollo.ApolloBasicDTO;
import com.choice.cloud.architect.groot.dto.apollo.ApolloCompareRequestDTO;
import com.ctrip.framework.apollo.openapi.dto.NamespaceReleaseDTO;
import com.ctrip.framework.apollo.openapi.dto.OpenAppNamespaceDTO;
import com.ctrip.framework.apollo.openapi.dto.OpenEnvClusterDTO;
import com.ctrip.framework.apollo.openapi.dto.OpenItemDTO;
import com.ctrip.framework.apollo.openapi.dto.OpenNamespaceDTO;
import com.ctrip.framework.apollo.openapi.dto.OpenNamespaceLockDTO;
import com.ctrip.framework.apollo.openapi.dto.OpenReleaseDTO;

public interface ApolloClientService {

    /**
     * 获取所有环境的集群信息
     * @param appId 应用ID
     * @return
     */
    List<OpenEnvClusterDTO> getAllEnvClusters(String appId);

    /**
     *  获取所有集群下的namespace
     * @param appId 应用ID
     * @param env 环境信息
     * @param clusterName 集群名称
     * @return
     */
    List<OpenNamespaceDTO> getAllClusterNamespaces(String appId, String env, String clusterName);

    /**
     *  获取某个集群下的namespace
     * @param appId
     * @param env
     * @param clusterName
     * @param namespaceName
     * @return
     */
    OpenNamespaceDTO getClusterAllNamespaces(String appId, String env, String clusterName, String namespaceName);

    /**
     * 创建namespace
     * @param appNamespaceDTO
     * @return
     */
    OpenAppNamespaceDTO createAppNamespace(OpenAppNamespaceDTO appNamespaceDTO);

    /**
     * 获取某个Namespace当前编辑人接口
     * Apollo在生产环境（PRO）有限制规则：每次发布只能有一个人编辑配置，
     * 且该次发布的人不能是该次发布的编辑人。
     * 也就是说如果一个用户A修改了某个namespace的配置，
     * 那么在这个namespace发布前，只能由A修改，其它用户无法修改。
     * 同时，该用户A无法发布自己修改的配置，必须找另一个有发布权限的人操作。
     * 这个接口就是用来获取当前namespace是否有人锁定的接口。
     * 在非生产环境（FAT、UAT），该接口始终返回没有人锁定
     * @param appId
     * @param env
     * @param clusterName
     * @param namespaceName
     * @return
     */
    OpenNamespaceLockDTO getNamespaceLock(String appId, String env, String clusterName, String namespaceName);

    /**
     * 创建配置
     * @param appId
     * @param env
     * @param clusterName
     * @param namespaceName
     * @param itemDTO
     * @return
     */
    OpenItemDTO createItem(String appId, String env, String clusterName, String namespaceName, OpenItemDTO itemDTO);

    /**
     * 修改配置
     * @param appId
     * @param env
     * @param clusterName
     * @param namespaceName
     * @param itemDTO
     */
    void updateItem(String appId, String env, String clusterName, String namespaceName, OpenItemDTO itemDTO);

    /**
     * 创建或更新配置
     * @param appId
     * @param env
     * @param clusterName
     * @param namespaceName
     * @param itemDTO
     */
    void createOrUpdateItem(String appId, String env, String clusterName, String namespaceName, OpenItemDTO itemDTO);

    /**
     * 删除配置
     * @param appId
     * @param env
     * @param clusterName
     * @param namespaceName
     * @param key
     * @param operator
     */
    void removeItem(String appId, String env, String clusterName, String namespaceName, String key, String operator);

    /**
     * 发布配置
     * @param appId
     * @param env
     * @param clusterName
     * @param namespaceName
     * @param releaseDTO
     * @return
     */
    OpenReleaseDTO publishNamespace(String appId, String env, String clusterName, String namespaceName, NamespaceReleaseDTO releaseDTO);

    /**
     * 获取某个Namespace当前生效的已发布配置接口
     * @param appId
     * @param env
     * @param clusterName
     * @param namespaceName
     * @return
     */
    OpenReleaseDTO getLatestActiveRelease(String appId, String env, String clusterName, String namespaceName);

    /**
     * 根据key获取配置项value
     * @param appId
     * @param env
     * @param cluster
     * @param key
     * @return
     */
    OpenItemDTO getOpenItemByKey(String appId, String env, String cluster, String key);

    /**
     * 获取所有配置项
     * @param appId
     * @param env
     * @param cluster
     * @return
     */
    List<OpenItemDTO> getAllOpenItem(String appId, String env, String cluster);

    /**
     * 比较Apollo配置内容的异同
     * @param compareRequest
     * @return
     */
    List<String> compareCluster(ApolloCompareRequestDTO compareRequest);

    /**
     * 获取所有Apollo配置，包括公共配置
     * @param basicDTO
     * @return
     */
    Map<String, String> findAllItem(ApolloBasicDTO basicDTO);

}
