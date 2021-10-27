package com.choice.cloud.architect.groot.service.inner.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.validation.constraints.NotBlank;

import com.alibaba.fastjson.JSON;

import com.choice.cloud.architect.groot.dto.CreateAppIdRequest;
import com.choice.cloud.architect.groot.enums.ApolloEnvEnum;
import com.choice.cloud.architect.groot.exception.ServiceException;
import com.choice.cloud.architect.groot.properties.ApolloClientConfig;
import com.choice.cloud.architect.groot.properties.ApolloClientProperties;
import com.choice.cloud.architect.groot.properties.ApolloServerProperties;
import com.choice.cloud.architect.groot.service.inner.CreateApolloService;
import com.ctrip.framework.apollo.openapi.client.ApolloOpenApiClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * @ClassName CreateApolloServiceImpl
 * @Description 通过Cookie远程调用Apollo相关接口
 * @Author LZ
 * @Date 2020/4/13 15:48
 * @Version 1.0
 */
@Service
@Slf4j
public class CreateApolloServiceImpl implements CreateApolloService {

    @Autowired
    private ApolloClientProperties apolloClientProperties;
    @Autowired
    private ApolloClientServiceImpl apolloClientService;
    @Autowired
    private ApolloServerProperties apolloServerProperties;

    /**
     * 创建AppId和idc
     *
     * @param request
     */
    @Override
    public void createAppIdAndIdc(CreateAppIdRequest request) {
        log.info("创建AppId时候的入参:{}", JSON.toJSONString(request));
        @NotBlank(message = "应用Id(AppId)不能为空") String appId = request.getAppId();
        List<String> envList = Arrays.asList(ApolloEnvEnum.FAT.getCode(), ApolloEnvEnum.PRO.getCode());
        for (String env : envList) {
            // 根据传入的env从Apollo配置文件中读取portalUrl
            String portalUrl = this.getApolloPortalUrl(env);
            // 获取对应env下的cookie
            String cookie = this.getCookie(portalUrl, env);
            // 创建AppId
            int responseCode = this.createAppId(request, portalUrl, cookie, env);
            if (HTTP_OK == responseCode) {
                // 创建idc
                this.createIdc(request.getAppId(), env, portalUrl, cookie);

                // 授权创建的appId
                boolean assignFlag = this.assign(appId, portalUrl, cookie, env);
                if (!assignFlag) {
                    log.error("授权应用失败!");
                }
                log.info("授权应用成功");
            }
        }
    }

    /**
     * 授权
     * @param appId
     * @param portalUrl
     * @param cookie
     * @param env
     * @return
     */
    private boolean assign(String appId, String portalUrl, String cookie, String env) {
        Assert.hasLength(appId, "appId不能为空");
        Assert.hasLength(portalUrl, "apollo地址不能为空");
        Assert.hasLength(cookie, "cookie不能为空");
        Assert.hasLength(appId, "env不能为空");

        ApolloOpenApiClient client = apolloClientService.getClientByEnv(env);
        String token = client.getToken();
        Assert.hasLength(token, "没有获取到相应的token");

        String assignUrl = "/consumers/" + token + "/assign-role?type=AppRole";
        String uri = portalUrl.concat(assignUrl);

        CloseableHttpClient httpClient = HttpClients.custom()
            .setConnectionTimeToLive(6000, TimeUnit.MILLISECONDS).build();
        HttpPost httpPost = new HttpPost(uri);
        httpPost.setHeader("Content-Type", "application/json;charset=utf8");
        httpPost.addHeader("Cookie", cookie);

        Map<String, String> map = new HashMap<>();
        map.put("appId", appId);
        map.put("namespaceName", "");
        // 将Object转换为json字符串;
        String jsonString = JSON.toJSONString(map);

        StringEntity entity = new StringEntity(jsonString, "UTF-8");

        // post请求是将参数放在请求体里面传过去的;这里将entity放入post请求体中
        httpPost.setEntity(entity);

        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
        } catch (IOException e) {
            log.error("远程调用Apollo授权接口失败,异常信息:{}", ExceptionUtils.getStackTrace(e));
            return false;
        }
        assert response != null;
        if (response.getStatusLine().getStatusCode() == HTTP_OK) {
            String s = null;
            try {
                s = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.displayName());
            } catch (IOException e) {
                log.error("转换相应信息时失败,异常信息:{}", ExceptionUtils.getStackTrace(e));
                return false;
            }
            log.info("远程调用Apollo授权接口响应码是200:{}", s);
        } else {
            String s = String.valueOf(response.getStatusLine().getStatusCode());
            log.info("远程调用Apollo授权接口响应码不是200:{}", s);
            return false;
        }
        return true;
    }

    /**
     * 创建Idc
     *
     * @param appId
     * @param env
     * @param baseUrl
     * @param cookie
     */
    public void createIdc(String appId, String env, String baseUrl, String cookie) {
        Assert.hasLength(appId, "创建idc时候appId不能为空");
        Assert.hasLength(env, "创建idc时候env不能为空");
        Assert.hasLength(baseUrl, "创建idc时候baseUrl不能为空");
        Assert.hasLength(cookie, "创建idc时候cookie不能为空");

        String createIdcUrl = "/apps/" + appId + "/envs/" + env + "/clusters";
        String uri = baseUrl.concat(createIdcUrl);

        // 判断传过来的env
        if (ApolloEnvEnum.FAT.getCode().equalsIgnoreCase(env)) {
            this.invokeIdc(appId, env, ApolloEnvEnum.DEV.getCode(), uri, cookie);
        } else if (ApolloEnvEnum.PRO.getCode().equalsIgnoreCase(env)) {
            this.invokeIdc(appId, env, ApolloEnvEnum.PRE.getCode(), uri, cookie);
        } else {
            log.error("传递过来的env不正确,请检查好在进行调用,该appId is:{}", appId);
        }
    }

    /**
     * 获取Cookie
     *
     * @return
     */
    public String getCookie(String baseUrl, String env) {
        Assert.hasLength(baseUrl, "Apollo地址不能为空");
        Assert.hasLength(env, "获取cookie时env不能为空");
        // 登陆 Url
        String loginUrl = baseUrl.concat("/signin");

        org.apache.commons.httpclient.HttpClient httpClient = new HttpClient();

        // 模拟登陆
        PostMethod postMethod = new PostMethod(loginUrl);

        // 设置登陆时要求的信息，用户名和密码
        if (ApolloEnvEnum.FAT.getCode().equalsIgnoreCase(env)) {
            NameValuePair[] data = {new NameValuePair("username", apolloServerProperties.getOfflineUsername()), new NameValuePair("password", apolloServerProperties.getOfflinePassword())};
            postMethod.setRequestBody(data);
        } else if (ApolloEnvEnum.PRO.getCode().equalsIgnoreCase(env)) {
            NameValuePair[] data = {new NameValuePair("username", apolloServerProperties.getOnlineUsername()), new NameValuePair("password", apolloServerProperties.getOnlinePassword())};
            postMethod.setRequestBody(data);
        } else {
            log.error("获取cookie时候发生失败，既不是线下也不是线上, env:{}, baseUrl:{}", env, baseUrl);
            return null;
        }

        try {
            // 设置 HttpClient 接收 Cookie,用与浏览器一样的策略
            httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
            // 这里的状态码为302，重定向
            int statusCode = httpClient.executeMethod(postMethod);

            if (Integer.valueOf(302).equals(statusCode)) {
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
            log.error("远程调用Apollo获取cookie失败，错误信息：{}", ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    /**
     * 根据传入的env从Apollo配置文件中读取portalUrl
     *
     * @param env
     * @return
     */
    private String getApolloPortalUrl(String env) {
        Assert.hasLength(env, "apollo env not blank");
        Map<String, ApolloClientConfig> config = apolloClientProperties.getConfig();
        for (Entry<String, ApolloClientConfig> entry : config.entrySet()) {
            if (entry.getValue().getEnvs().contains(env.toUpperCase())) {
                return entry.getValue().getPortalUrl();
            }
        }
        throw new ServiceException("apollo portalUrl not found ！！！");
    }

    /**
     * 创建AppId
     *
     * @param request
     * @param baseUrl
     * @param cookie
     * @param env
     * @return
     */
    private int createAppId(CreateAppIdRequest request, String baseUrl, String cookie, String env) {
        // http://apolload.dev.choicesaas.cn/apps

        Assert.hasLength(baseUrl, "Apollo地址不能为空");
        Assert.hasLength(cookie, "cookie不能为空");
        Assert.hasLength(env, "env不能为空");

        String createAppIdUrl = "/apps";
        String uri = baseUrl.concat(createAppIdUrl);

        CloseableHttpClient httpClient = HttpClients.custom()
            .setConnectionTimeToLive(6000, TimeUnit.MILLISECONDS).build();
        HttpPost httpPost = new HttpPost(uri);
        httpPost.setHeader("Content-Type", "application/json;charset=utf8");
        httpPost.addHeader("Cookie", cookie);

        // 将request转换为json字符串;
        String jsonString = JSON.toJSONString(request);

        StringEntity entity = new StringEntity(jsonString, "UTF-8");

        // post请求是将参数放在请求体里面传过去的;这里将entity放入post请求体中
        httpPost.setEntity(entity);

        String result = null;
        try {
            log.info("创建appId时的参数:{}", jsonString);
            CloseableHttpResponse response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HTTP_OK) {
                result = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.displayName());
                log.info("在创建appId时候，创建成功，调用Apollo返回信息:{}", result);
                return response.getStatusLine().getStatusCode();
            } else {
                result = String.valueOf(response.getStatusLine().getStatusCode());
                if ("302".equalsIgnoreCase(result)) {
                    log.error("cookie不正确或者已经失效,调用Apollo返回信息:{}", result);
                } else {
                    log.error("在创建appId时候，创建失败，调用Apollo返回信息:{}", result);
                }
                return response.getStatusLine().getStatusCode();
            }
        } catch (IOException e) {
            log.error("远程调用Apollo来创建appId时发生异常,入参:{},异常:{}", jsonString, e);
        }
        return 0;
    }

    /**
     * 远程调用Apollo创建Idc
     *
     * @param appId
     * @param env
     * @param idc
     * @param uri
     * @param cookie
     */
    private void invokeIdc(String appId, String env, String idc, String uri, String cookie) {
        CloseableHttpClient httpClient = HttpClients.custom()
            .setConnectionTimeToLive(6000, TimeUnit.MILLISECONDS).build();
        HttpPost httpPost = new HttpPost(uri);
        httpPost.setHeader("Content-Type", "application/json;charset=utf8");
        httpPost.setHeader("Cookie", cookie);

        Map<String, String> map = new HashMap<>();
        map.put("name", idc);
        map.put("appId", appId);
        // 将Object转换为json字符串;
        String jsonString = JSON.toJSONString(map);

        StringEntity entity = new StringEntity(jsonString, "UTF-8");

        // post请求是将参数放在请求体里面传过去的;这里将entity放入post请求体中
        httpPost.setEntity(entity);

        String result = null;
        try {
            CloseableHttpResponse response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HTTP_OK) {
                result = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.displayName());
                log.info("在【" + appId + "】下的【" + env + "】环境下创建【" + idc + "】时候，调用Apollo返回信息：" + result);
            } else {
                result = String.valueOf(response.getStatusLine().getStatusCode());
                // 400
                if ("400".equalsIgnoreCase(result)) {
                    log.info("在【" + appId + "】下的【" + env + "】环境下已经存在了【" + idc + "】，调用Apollo返回信息：" + result);
                    // 403
                    // Apollo中不存在该appId
                } else if ("403".equalsIgnoreCase(result)) {
                    log.error("没有找到appId,该appId is:{},调用Apollo返回信息:{}", appId, result);
                } else if ("302".equalsIgnoreCase(result)) {
                    log.error("cookie不正确或者已经失效,调用Apollo返回信息:{}", result);
                }
            }
        } catch (IOException e) {
            log.error("远程调用Apollo来创建idc时发生失败,appId is {},env is {}, clusterName:{}", appId, env, idc);
        }
    }
}
