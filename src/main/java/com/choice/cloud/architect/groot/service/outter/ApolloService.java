package com.choice.cloud.architect.groot.service.outter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;

import com.aliyun.openservices.shade.org.apache.commons.lang3.exception.ExceptionUtils;
import com.choice.cloud.architect.groot.dto.apollo.ApolloCreateUserRequestDTO;
import com.choice.cloud.architect.groot.dto.apollo.ApolloPageQueryCommitItemHistoryListResponseDTO;
import com.choice.cloud.architect.groot.dto.apollo.ApolloQueryAllItemByEnvIdcNamespaceResponseDTO;
import com.choice.cloud.architect.groot.enums.ApolloEnvEnum;
import com.choice.cloud.architect.groot.util.JsonListUtil;
import com.choice.cloud.architect.groot.util.JsonUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import static java.net.HttpURLConnection.HTTP_MOVED_TEMP;
import static java.net.HttpURLConnection.HTTP_OK;

/**
 * <p>
 * Apollo服务端业务接口
 * </p>
 *
 * @author LZ
 * @date Created in 2020/6/15 15:48
 */
@Service
@Slf4j
public class ApolloService {

    @Value("${apollo.client.config.offline.portalUrl}")
    private String offlinePortalUrl;

    @Value("${apollo.client.config.online.portalUrl}")
    private String onlinePortalUrl;

    @Value("${apollo.server.offline.username}")
    private String offlineUsername;

    @Value("${apollo.server.offline.password}")
    private String offlinePassword;

    @Value("${apollo.server.online.username}")
    private String onlineUsername;

    @Value("${apollo.server.online.password}")
    private String onlinePassword;

    /**
     * 获取Cookie
     *
     * @param portalUrl
     * @param env
     * @return
     */
    public String getCookie(String portalUrl, String env) {
        Assert.hasLength(portalUrl, "获取cookie时Apollo访问地址不能为空");
        Assert.hasLength(env, "获取cookie时env不能为空");
        // 登陆 Url
        String loginUrl = portalUrl.concat("/signin");

        org.apache.commons.httpclient.HttpClient httpClient = new HttpClient();

        // 模拟登陆
        PostMethod postMethod = new PostMethod(loginUrl);

        // 设置登陆时要求的信息，用户名和密码
        if (ApolloEnvEnum.FAT.getCode().equalsIgnoreCase(env)) {
            NameValuePair[] data = {new NameValuePair("username", offlineUsername), new NameValuePair("password", offlinePassword)};
            postMethod.setRequestBody(data);
        } else if (ApolloEnvEnum.PRO.getCode().equalsIgnoreCase(env)) {
            NameValuePair[] data = {new NameValuePair("username", onlineUsername), new NameValuePair("password", onlinePassword)};
            postMethod.setRequestBody(data);
        } else {
            log.error("获取cookie时候发生失败，既不是线下也不是线上, env:{}, portalUrl:{}", env, portalUrl);
            return "";
        }

        try {
            // 设置 HttpClient 接收 Cookie,用与浏览器一样的策略
            httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
            // 这里的状态码为302，重定向
            int statusCode = httpClient.executeMethod(postMethod);

            if (Integer.valueOf(HTTP_MOVED_TEMP).equals(statusCode)) {
                // 证明模拟登陆成功
                // 获得登陆后的 Cookie
                org.apache.commons.httpclient.Cookie[] cookies = httpClient.getState().getCookies();
                StringBuilder tmpCookies = new StringBuilder();
                for (org.apache.commons.httpclient.Cookie c : cookies) {
                    tmpCookies.append(c.toString()).append(";");
                }
                return tmpCookies.toString();
            }
        } catch (Exception e) {
            log.error("远程调用Apollo获取cookie失败，错误信息：{}", org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    /**
     * 根据环境、集群和命名空间获取该条件下所有的配置项
     * http://localhost:8070/apps/apollo-test/envs/DEV/clusters/default/namespaces/application
     *
     * @param portalUrl
     * @param appId
     * @param env
     * @param idc
     * @param namespace
     * @return
     */
    public ApolloQueryAllItemByEnvIdcNamespaceResponseDTO queryAllItemByEnvIdcNamespace(String portalUrl, String appId, String env, String idc,
        String namespace) {
        Assert.hasLength(env, "环境不能为空");
        Assert.hasLength(idc, "集群不能为空");
        Assert.hasLength(namespace, "命名空间不能为空");

        ApolloQueryAllItemByEnvIdcNamespaceResponseDTO responseDTO = new ApolloQueryAllItemByEnvIdcNamespaceResponseDTO();
        responseDTO.setItems(Lists.newArrayList());
        CloseableHttpClient httpClient = HttpClients.custom()
            .setConnectionTimeToLive(6000, TimeUnit.MILLISECONDS).build();

        String fullUrl = String.format("%s/apps/%s/envs/%s/clusters/%s/namespaces/%s", portalUrl, appId, env, idc, namespace);

        log.info("准备远程调用Apollo根据环境、集群和命名空间获取该条件下所有的配置项时的完整url:{}", fullUrl);

        /*
         * 由于GET请求的参数都是拼装在URL地址后方，所以要构建一个URL，带参数
         */
        HttpGet httpGet = null;
        try {
            URIBuilder uriBuilder = new URIBuilder(fullUrl);
            httpGet = new HttpGet(uriBuilder.build());
        } catch (URISyntaxException e) {
            log.error("远程调用Apollo根据环境、集群和命名空间获取该条件下所有的配置项时构建请求参数URL出现失败：{}", ExceptionUtils.getStackTrace(e));
            return null;
        }

        httpGet.setHeader("Content-Type", "application/json;charset=utf8");
        httpGet.setHeader("Cookie", this.getCookie(portalUrl, env));

        try {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == HTTP_OK) {
                String result = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.displayName());
                log.info("远程调用Apollo根据环境、集群和命名空间获取该条件下所有的配置项时结果：{}", result);
                responseDTO = JsonUtil.getObject(result, ApolloQueryAllItemByEnvIdcNamespaceResponseDTO.class);
                log.info("远程调用Apollo根据环境、集群和命名空间获取该条件下所有的配置项时json转化实体结果：{}", responseDTO);
                return responseDTO;
            } else {
                String result = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.displayName());
                String resultStatus = String.valueOf(response.getStatusLine().getStatusCode());
                if (resultStatus.equalsIgnoreCase(String.valueOf(HTTP_MOVED_TEMP))) {
                    log.error("cookie不正确或者已经失效,调用Apollo返回信息:{}", result);
                } else {
                    log.error("远程调用Apollo根据环境、集群和命名空间获取该条件下所有的配置项时失败，调用Apollo返回信息:{}", result);
                }
                return responseDTO;
            }
        } catch (IOException e) {
            log.error("远程调用Apollo根据环境、集群和命名空间获取该条件下所有的配置项时发生异常，异常:{}", ExceptionUtils.getStackTrace(e));
            return responseDTO;
        }
    }

    /**
     * 应用级别赋权
     * 应用级别权限包括: 创建Namespace、修改或发布应用下任何Namespace
     * http://localhost:8070/consumers/1003d0472a2fea1cc74a33e0dfe71452f2db0ed3/assign-role?type=AppRole
     *
     * @param appId       被管理的appId
     * @param portalUrl
     * @param assignToken
     * @param env
     * @return
     */
    public boolean assignByAppRoleType(String appId, String portalUrl, String assignToken, String env) {
        Assert.hasLength(appId, "appId不能为空");
        Assert.hasLength(portalUrl, "apollo地址不能为空");
        Assert.hasLength(env, "apollo环境不能为空");

        CloseableHttpClient httpClient = HttpClients.custom()
            .setConnectionTimeToLive(6000, TimeUnit.MILLISECONDS).build();

        String fullUrl = String.format("%s/consumers/%s/assign-role?type=AppRole", portalUrl, assignToken);

        log.info("准备远程调用Apollo应用级别赋权接口时的完整url:{}", fullUrl);

        HttpPost httpPost = new HttpPost(fullUrl);
        httpPost.setHeader("Content-Type", "application/json;charset=utf8");
        httpPost.setHeader("Cookie", this.getCookie(portalUrl, env));

        Map<String, String> map = new HashMap<>();
        map.put("appId", appId);
        // 将Object转换为json字符串;
        String jsonString = JSON.toJSONString(map);

        StringEntity entity = new StringEntity(jsonString, "UTF-8");

        // post请求是将参数放在请求体里面传过去的;这里将entity放入post请求体中
        httpPost.setEntity(entity);
        log.info("远程调用Apollo应用级别赋权接口时的参数:{}", jsonString);
        try {
            CloseableHttpResponse response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HTTP_OK) {
                String result = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.displayName());
                log.info("远程调用Apollo应用级别赋权接口时结果：{}", result);
                return true;
            } else {
                String result = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.displayName());
                String statusCode = String.valueOf(response.getStatusLine().getStatusCode());
                if (statusCode.equalsIgnoreCase(String.valueOf(HTTP_MOVED_TEMP))) {
                    log.error("cookie不正确或者已经失效,调用Apollo返回信息:{}", result);
                }
                log.error("远程调用Apollo应用级别赋权接口时失败,入参:{},返参状态码:{},结果:{}", jsonString, statusCode, result);
                return false;
            }
        } catch (IOException e) {
            log.error("远程调用Apollo应用级别赋权接口时发生异常,入参:{},异常:{}", jsonString, ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    /**
     * 创建用户
     * http://localhost:8070/users
     *
     * @param requestDTO
     * @param portalUrl
     * @param env
     * @return
     */
    public boolean createUser(ApolloCreateUserRequestDTO requestDTO, String portalUrl, String env) {
        CloseableHttpClient httpClient = HttpClients.custom()
            .setConnectionTimeToLive(6000, TimeUnit.MILLISECONDS).build();

        String fullUrl = String.format("%s/users", portalUrl);

        log.info("准备远程调用Apollo创建用户时的完整url:{}", fullUrl);

        HttpPost httpPost = new HttpPost(fullUrl);
        httpPost.setHeader("Content-Type", "application/json;charset=utf8");
        httpPost.setHeader("Cookie", this.getCookie(portalUrl, env));

        // 将Object转换为json字符串;
        String jsonString = JSON.toJSONString(requestDTO);

        StringEntity entity = new StringEntity(jsonString, "UTF-8");

        // post请求是将参数放在请求体里面传过去的;这里将entity放入post请求体中
        httpPost.setEntity(entity);
        log.info("远程调用Apollo创建用户时的参数:{}", jsonString);
        try {
            CloseableHttpResponse response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HTTP_OK) {
                String result = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.displayName());
                log.info("远程调用Apollo创建用户时结果：{}", result);
                return true;
            } else {
                String result = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.displayName());
                String statusCode = String.valueOf(response.getStatusLine().getStatusCode());
                if (statusCode.equalsIgnoreCase(String.valueOf(HTTP_MOVED_TEMP))) {
                    log.error("cookie不正确或者已经失效,调用Apollo返回信息:{}", result);
                    return false;
                } else {
                    log.error("远程调用Apollo创建用户时失败,入参:{},返参状态码:{},结果:{}", jsonString, statusCode, result);
                    return false;
                }
            }
        } catch (IOException e) {
            log.error("远程调用Apollo创建用户时发生异常,入参:{},异常:{}", jsonString, ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    /**
     * 权限管理-赋予用户应用修改权（可以修改配置）
     * http://localhost:8070/apps/test-assign-demo/namespaces/application/roles/ModifyNamespace
     *
     * @param portalUrl
     * @param env
     * @param appId
     * @param namespace
     * @param username
     * @return
     */
    public boolean assignAppUserModifyNamespace(String portalUrl, String env, String appId, String namespace, String username) {
        CloseableHttpClient httpClient = HttpClients.custom()
            .setConnectionTimeToLive(6000, TimeUnit.MILLISECONDS).build();

        String fullUrl = String.format("%s/apps/%s/namespaces/%s/roles/ModifyNamespace", portalUrl, appId, namespace);

        log.info("准备远程调用Apollo赋予用户应用修改权时的完整url:{}", fullUrl);

        HttpPost httpPost = new HttpPost(fullUrl);
        httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");
        httpPost.setHeader("Cookie", this.getCookie(portalUrl, env));

        StringEntity entity = new StringEntity(username, "UTF-8");

        // post请求是将参数放在请求体里面传过去的;这里将entity放入post请求体中
        httpPost.setEntity(entity);
        log.info("远程调用Apollo赋予用户应用修改权时的用户名为:{}", username);
        try {
            CloseableHttpResponse response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HTTP_OK) {
                String result = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.displayName());
                log.info("远程调用Apollo赋予用户应用修改权时结果：{}", result);
                return true;
            } else {
                String result = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.displayName());
                String statusCode = String.valueOf(response.getStatusLine().getStatusCode());
                if (statusCode.equalsIgnoreCase(String.valueOf(HTTP_MOVED_TEMP))) {
                    log.error("cookie不正确或者已经失效,调用Apollo返回信息:{}", result);
                    return false;
                } else {
                    log.error("远程调用Apollo赋予用户应用修改权时失败,入参用户名:{},返参状态码:{},结果:{}", username, statusCode, result);
                    return false;
                }
            }
        } catch (IOException e) {
            log.error("远程调用Apollo赋予用户应用修改权时发生异常,入参用户名:{},应用名:{},环境:{},命名空间:{},异常:{}", username, appId, env, namespace, ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    /**
     * 权限管理-赋予用户应用发布权（可以发布配置）
     * http://localhost:8070/apps/ccc/namespaces/application/roles/ReleaseNamespace
     *
     * @param portalUrl
     * @param env
     * @param appId
     * @param namespace
     * @param username
     * @return
     */
    public boolean assignAppUserReleaseNamespace(String portalUrl, String env, String appId, String namespace, String username) {
        CloseableHttpClient httpClient = HttpClients.custom()
            .setConnectionTimeToLive(6000, TimeUnit.MILLISECONDS).build();

        String fullUrl = String.format("%s/apps/%s/namespaces/%s/roles/ReleaseNamespace", portalUrl, appId, namespace);

        log.info("准备远程调用Apollo赋予用户应用发布权时的完整url:{}", fullUrl);

        HttpPost httpPost = new HttpPost(fullUrl);
        httpPost.setHeader("Content-Type", "text/plain;charset=UTF-8");
        httpPost.setHeader("Cookie", this.getCookie(portalUrl, env));

        StringEntity entity = new StringEntity(username, "UTF-8");

        // post请求是将参数放在请求体里面传过去的;这里将entity放入post请求体中
        httpPost.setEntity(entity);
        log.info("远程调用Apollo赋予用户应用发布权时的用户名为:{}", username);
        try {
            CloseableHttpResponse response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HTTP_OK) {
                String result = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.displayName());
                log.info("远程调用Apollo赋予用户应用发布权时结果：{}", result);
                return true;
            } else {
                String result = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.displayName());
                String statusCode = String.valueOf(response.getStatusLine().getStatusCode());
                if (statusCode.equalsIgnoreCase(String.valueOf(HTTP_MOVED_TEMP))) {
                    log.error("cookie不正确或者已经失效,调用Apollo返回信息:{}", result);
                    return false;
                } else {
                    log.error("远程调用Apollo赋予用户应用发布权时失败,入参用户名:{},返参状态码:{},结果:{}", username, statusCode, result);
                    return false;
                }
            }
        } catch (IOException e) {
            log.error("远程调用Apollo赋予用户应用发布权时发生异常,入参用户名:{},应用名:{},环境:{},命名空间:{},异常:{}", username, appId, env, namespace, ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    /**
     * 删除 权限管理-赋予用户应用修改权（可以修改配置）
     * http://apolload.dev.choicesaas.cn/apps/pterosaur/namespaces/application/roles/ModifyNamespace?user=liuzhen2
     * delete
     *
     * @param portalUrl
     * @param env
     * @param appId
     * @param namespace
     * @param username
     * @return
     */
    public boolean deleteAppUserModifyNamespace(String portalUrl, String env, String appId, String namespace, String username) {
        CloseableHttpClient httpClient = HttpClients.custom()
            .setConnectionTimeToLive(6000, TimeUnit.MILLISECONDS).build();

        String fullUrl = String.format("%s/apps/%s/namespaces/%s/roles/ModifyNamespace?user=%s", portalUrl, appId, namespace, username);

        log.info("准备远程调用Apollo删除用户应用修改权时的完整url:{}", fullUrl);

        HttpDelete httpDelete = new HttpDelete(fullUrl);
        httpDelete.setHeader("Content-Type", "application/json;charset=UTF-8");
        httpDelete.setHeader("Cookie", this.getCookie(portalUrl, env));

        log.info("远程调用Apollo删除用户应用修改权时的用户名为:{}", username);
        try {
            CloseableHttpResponse response = httpClient.execute(httpDelete);
            if (response.getStatusLine().getStatusCode() == HTTP_OK) {
                String result = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.displayName());
                log.info("远程调用Apollo删除用户应用修改权时结果：{}", result);
                return true;
            } else {
                String result = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.displayName());
                String statusCode = String.valueOf(response.getStatusLine().getStatusCode());
                if (statusCode.equalsIgnoreCase(String.valueOf(HTTP_MOVED_TEMP))) {
                    log.error("cookie不正确或者已经失效,调用Apollo返回信息:{}", result);
                    return false;
                } else {
                    log.error("远程调用Apollo删除用户应用修改权时失败,入参用户名:{},返参状态码:{},结果:{}", username, statusCode, result);
                    return false;
                }
            }
        } catch (IOException e) {
            log.error("远程调用Apollo删除用户应用修改权时发生异常,入参用户名:{},应用名:{},环境:{},命名空间:{},异常:{}", username, appId, env, namespace, ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    /**
     * 删除 权限管理-赋予用户应用发布权（可以发布配置）
     * http://apolload.dev.choicesaas.cn/apps/pterosaur/namespaces/application/roles/ReleaseNamespace?user=liuzhen2
     * delete
     *
     * @param portalUrl
     * @param env
     * @param appId
     * @param namespace
     * @param username
     * @return
     */
    public boolean deleteAppUserReleaseNamespace(String portalUrl, String env, String appId, String namespace, String username) {
        CloseableHttpClient httpClient = HttpClients.custom()
            .setConnectionTimeToLive(6000, TimeUnit.MILLISECONDS).build();

        String fullUrl = String.format("%s/apps/%s/namespaces/%s/roles/ReleaseNamespace?user=%s", portalUrl, appId, namespace, username);

        log.info("准备远程调用Apollo删除用户应用发布权时的完整url:{}", fullUrl);

        HttpDelete httpDelete = new HttpDelete(fullUrl);
        httpDelete.setHeader("Content-Type", "text/plain;charset=UTF-8");
        httpDelete.setHeader("Cookie", this.getCookie(portalUrl, env));

        log.info("远程调用Apollo删除用户应用发布权时的用户名为:{}", username);
        try {
            CloseableHttpResponse response = httpClient.execute(httpDelete);
            if (response.getStatusLine().getStatusCode() == HTTP_OK) {
                String result = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.displayName());
                log.info("远程调用Apollo删除用户应用发布权时结果：{}", result);
                return true;
            } else {
                String result = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.displayName());
                String statusCode = String.valueOf(response.getStatusLine().getStatusCode());
                if (statusCode.equalsIgnoreCase(String.valueOf(HTTP_MOVED_TEMP))) {
                    log.error("cookie不正确或者已经失效,调用Apollo返回信息:{}", result);
                    return false;
                } else {
                    log.error("远程调用Apollo删除用户应用发布权时失败,入参用户名:{},返参状态码:{},结果:{}", username, statusCode, result);
                    return false;
                }
            }
        } catch (IOException e) {
            log.error("远程调用Apollo删除用户应用发布权时发生异常,入参用户名:{},应用名:{},环境:{},命名空间:{},异常:{}", username, appId, env, namespace, ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    /**
     * 分页查询应用的某个环境集群命名空间下的已更改历史配置信息
     * http://apolload.dev.choicesaas.cn/apps/pterosaur/envs/FAT/clusters/DEV/namespaces/application/commits?page=0&size=10
     * GET
     *
     * @param portalUrl
     * @param appId
     * @param env
     * @param idc
     * @param namespace
     * @param page
     * @param size
     * @return
     */
    public List<ApolloPageQueryCommitItemHistoryListResponseDTO> pageQueryCommitItemHistoryList(String portalUrl, String appId, String env, String idc,
        String namespace, Integer page, Integer size) {
        Assert.hasLength(portalUrl, "portal地址不能为空");
        Assert.hasLength(appId, "应用id不能为空");
        Assert.hasLength(env, "环境不能为空");
        Assert.hasLength(idc, "集群不能为空");
        Assert.hasLength(namespace, "命名空间不能为空");
        page = ObjectUtils.defaultIfNull(page, 0);
        size = ObjectUtils.defaultIfNull(size, 10);

        List<ApolloPageQueryCommitItemHistoryListResponseDTO> resultList = Lists.newArrayList();

        List<ApolloPageQueryCommitItemHistoryListResponseDTO> apolloResponseDTOList = Lists.newArrayList();

        CloseableHttpClient httpClient = HttpClients.custom()
            .setConnectionTimeToLive(6000, TimeUnit.MILLISECONDS).build();

        String fullUrl = String.format("%s/apps/%s/envs/%s/clusters/%s/namespaces/%s/commits", portalUrl, appId, env, idc, namespace);

        log.info("准备远程调用Apollo分页查询应用的某个环境集群命名空间下的已更改历史配置信息时的完整url:{}", fullUrl);

        /*
         * 由于GET请求的参数都是拼装在URL地址后方，所以要构建一个URL，带参数
         */
        HttpGet httpGet = null;
        try {
            URIBuilder uriBuilder = new URIBuilder(fullUrl);
            uriBuilder.addParameter("page", page.toString());
            uriBuilder.addParameter("size", size.toString());
            httpGet = new HttpGet(uriBuilder.build());
        } catch (URISyntaxException e) {
            log.error("远程调用Apollo分页查询应用的某个环境集群命名空间下的已更改历史配置信息时构建请求参数URL出现失败：{}", ExceptionUtils.getStackTrace(e));
            return null;
        }

        httpGet.setHeader("Content-Type", "application/json;charset=utf8");
        httpGet.setHeader("Cookie", this.getCookie(portalUrl, env));

        try {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == HTTP_OK) {
                String result = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.displayName());
                log.info("远程调用Apollo分页查询应用的某个环境集群命名空间下的已更改历史配置信息时结果：{}", result);
                apolloResponseDTOList = JsonListUtil.jsonToList(result, ApolloPageQueryCommitItemHistoryListResponseDTO.class);
                log.info("远程调用Apollo分页查询应用的某个环境集群命名空间下的已更改历史配置信息时json转化实体结果：{}", apolloResponseDTOList);
                if (CollectionUtils.isNotEmpty(apolloResponseDTOList)) {
                    for (ApolloPageQueryCommitItemHistoryListResponseDTO responseDTO : apolloResponseDTOList) {
                        String jsonChangeSets = responseDTO.getChangeSets();
                        ApolloPageQueryCommitItemHistoryListResponseDTO resultDTO = JsonUtil.getObject(jsonChangeSets, ApolloPageQueryCommitItemHistoryListResponseDTO.class);
                        //if (null == response) {
                        //    response = new ApolloPageQueryCommitItemHistoryListResponseDTO();
                        //}
                        //response.setAppId(responseDTO.getAppId());
                        //response.setClusterName(responseDTO.getClusterName());
                        //response.setNamespaceName(responseDTO.getNamespaceName());
                        //response.setDataChangeCreatedBy(responseDTO.getDataChangeCreatedBy());
                        //response.setDataChangeLastModifiedBy(responseDTO.getDataChangeLastModifiedBy());
                        //response.setDataChangeCreatedTime(responseDTO.getDataChangeCreatedTime());
                        //response.setDataChangeLastModifiedTime(responseDTO.getDataChangeLastModifiedTime());

                        resultList.add(resultDTO);
                    }
                }
                return resultList;
            } else {
                String result = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.displayName());
                String resultStatus = String.valueOf(response.getStatusLine().getStatusCode());
                if (resultStatus.equalsIgnoreCase(String.valueOf(HTTP_MOVED_TEMP))) {
                    log.error("cookie不正确或者已经失效,调用Apollo返回信息:{}", result);
                } else {
                    log.error("远程调用Apollo分页查询应用的某个环境集群命名空间下的已更改历史配置信息时失败，调用Apollo返回信息:{}", result);
                }
                return resultList;
            }
        } catch (IOException e) {
            log.error("远程调用Apollo分页查询应用的某个环境集群命名空间下的已更改历史配置信息时发生异常，异常:{}", ExceptionUtils.getStackTrace(e));
            return resultList;
        }
    }
}
