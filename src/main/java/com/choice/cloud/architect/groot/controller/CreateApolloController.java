package com.choice.cloud.architect.groot.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import com.alibaba.fastjson.JSON;

import com.choice.cloud.architect.groot.dto.CreateAppIdRequest;
import com.choice.cloud.architect.groot.dto.CreateAppIdResponse;
import com.choice.cloud.architect.groot.dto.CreateIdcRequest;
import com.choice.cloud.architect.groot.dto.CreateIdcResponse;
import com.choice.cloud.architect.groot.exception.ServiceException;
import com.choice.cloud.architect.groot.properties.ApolloClientConfig;
import com.choice.cloud.architect.groot.properties.ApolloClientProperties;
import com.choice.cloud.architect.groot.response.ResponseData;
import com.choice.cloud.architect.groot.service.inner.CreateApolloService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * @ClassName CreateApolloController
 * @Description 通过Cookie远程调用Apollo相关接口的controller
 * @Author LZ
 * @Date 2020/4/9 17:20
 * @Version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/test/api/apolloChange")
public class CreateApolloController {

    @Autowired
    private ApolloClientProperties apolloClientProperties;
    @Autowired
    private CreateApolloService createApolloService;

    @PostMapping("/create")
    public ResponseData<CreateIdcResponse> create(@RequestBody @Valid CreateAppIdRequest request) throws IOException {
        request.setOwnerName("apollo");
        createApolloService.createAppIdAndIdc(request);
        return ResponseData.createBySuccess();
    }

    /**
     * 创建idc接口
     *
     * @param createIdcRequest
     * @return
     */
    @PostMapping("/createIdc")
    public ResponseData<CreateIdcResponse> createIdc(@RequestBody @Valid CreateIdcRequest createIdcRequest) throws IOException {
        Assert.hasLength(createIdcRequest.getAppIds(), "应用id列表appIds不能为空，用英文逗号隔开的字符串");
        Assert.hasLength(createIdcRequest.getCookie(), "cookie不能为空，区分线上和线下");
        Assert.hasLength(createIdcRequest.getEnv(), "env不能为空，线上PRO，线下PRE");

        // 根据传入的env从Apollo配置文件中读取portalUrl
        String portalUrl = this.getApolloPortalUrl(createIdcRequest.getEnv());
        String cookie1 = this.getCookie(portalUrl);
        Assert.hasLength(cookie1, "没有获取到cookie");
        createIdcRequest.setCookie(cookie1);

        Map<String, Integer> countMap = new HashMap<>();
        countMap.put("num", 0);
        countMap.put("haveExistIdcNum", 0);
        countMap.put("errorAppIdNum", 0);

        CreateIdcResponse response = new CreateIdcResponse();
        // 不存在的appId集合记录下来，需要手动去Apollo新建项目
        List<String> errorAppIdList = new ArrayList<>();
        @NotBlank(message = "cookie不能为空，区分线上和线下") String cookie = createIdcRequest.getCookie();
        String[] split = createIdcRequest.getAppIds().split(",");
        for (String appId : split) {
            @NotBlank(message = "env不能为空，线上PRO，线下PRE") String env = createIdcRequest.getEnv();
            // 检查appId是否存在
            try {
                this.checkAppId(appId, portalUrl, cookie, errorAppIdList);
            } catch (IOException e) {
                log.error("远程调用Apollo来检查appId是否存在时发生失败,appId is:{}", appId);
                continue;
            }

            String result = null;
            // 判断传过来的env
            if ("FAT".equalsIgnoreCase(env)) {
                result = this.checkAndCreate("DEV", "FAT", appId, cookie, errorAppIdList, countMap);
                //this.checkAndCreate("DEFAULT", "FAT", appId);
            } else if ("PRO".equalsIgnoreCase(env)) {
                result = this.checkAndCreate("PRE", "PRO", appId, cookie, errorAppIdList, countMap);
                //this.checkAndCreate("DEFAULT", "PRO", appId);
            } else {
                log.error("传递过来的env不正确,请检查好在进行调用,该appId is:{}", appId);
            }

            // cookie不正确
            if (result == null || "302".equalsIgnoreCase(result)) {
                response.setDesc("cookie不正确或者cookie已经失效，请重新填写cookie");
                return ResponseData.createBySuccess(response);
            }
        }
        response.setErrorAppIdList(errorAppIdList);
        log.info("总共appId个数为{}", split.length);
        log.info("总共创建了{}个集群", countMap.get("num"));
        log.info("已经存在的集群{}个", countMap.get("haveExistIdcNum"));
        log.info("不正确的appId有{}个", countMap.get("errorAppIdNum"));
        return ResponseData.createBySuccess(response);
    }

    /**
     * 创建AppId接口
     *
     * @param createAppIdRequest
     * @return
     */
    @PostMapping("/createAppId")
    public ResponseData<CreateAppIdResponse> createAppId(@RequestBody @Valid CreateAppIdRequest createAppIdRequest) {
        //@NotBlank(message = "环境不能为空") String env = createAppIdRequest.getEnv();
        List<String> envList = Arrays.asList("FAT", "PRO");
        for (String env : envList) {
            String portalUrl = this.getApolloPortalUrl(env);
            String cookie = this.getCookie(portalUrl);
            this.createAppId(createAppIdRequest, portalUrl, cookie);
        }
        return ResponseData.createBySuccess();
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
     * 检查idc是否存在，不存在则创建idc
     *
     * @param clusterName
     * @param env
     * @param appId
     * @param cookie
     * @param errorAppIdList
     * @param countMap
     * @return
     */
    private String checkAndCreate(String clusterName, String env, String appId, String cookie, List<String> errorAppIdList,
        Map<String, Integer> countMap) {
        // 根据传入的env从Apollo配置文件中读取portalUrl
        String portalUrl = getApolloPortalUrl(env);
        // 创建idc
        String result = createIdc(appId, env, clusterName, portalUrl, cookie, errorAppIdList, countMap);
        return result;
    }

    /**
     * 创建idc
     *
     * @param appId
     * @param env
     * @param clusterName
     * @param baseUrl
     * @param cookie
     * @param errorAppIdList
     * @param countMap
     * @return
     */
    private String createIdc(String appId, String env, String clusterName, String baseUrl, String cookie, List<String> errorAppIdList,
        Map<String, Integer> countMap) {

        Assert.hasLength(appId, "appId不能为空");
        Assert.hasLength(clusterName, "idc不能为空");
        String createIdcUrl = "/apps/" + appId + "/envs/" + env + "/clusters";
        Assert.hasLength(baseUrl, "apollo地址不能为空");
        String uri = baseUrl.concat(createIdcUrl);

        CloseableHttpClient httpClient = HttpClients.custom()
            .setConnectionTimeToLive(6000, TimeUnit.MILLISECONDS).build();
        HttpPost httpPost = new HttpPost(uri);
        httpPost.setHeader("Content-Type", "application/json;charset=utf8");
        httpPost.setHeader("Cookie", cookie);

        Map<String, String> map = new HashMap<>();
        map.put("name", clusterName);
        map.put("appId", appId);
        // 将Object转换为json字符串;
        String jsonString = JSON.toJSONString(map);

        StringEntity entity = new StringEntity(jsonString, "UTF-8");

        // post请求是将参数放在请求体里面传过去的;这里将entity放入post请求体中
        httpPost.setEntity(entity);

        String s = null;
        try {
            CloseableHttpResponse response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HTTP_OK) {
                s = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.displayName());
                log.info("在【" + appId + "】下的【" + env + "】环境下创建【" + clusterName + "】时候，调用Apollo返回信息：" + s);
                System.out.println(s);
                countMap.put("num", countMap.get("num") + 1);
            } else {
                s = String.valueOf(response.getStatusLine().getStatusCode());
                // 400
                if ("400".equalsIgnoreCase(s)) {
                    log.info("在【" + appId + "】下的【" + env + "】环境下已经存在了【" + clusterName + "】，调用Apollo返回信息：" + s);
                    System.out.println(s);
                    countMap.put("haveExistIdcNum", countMap.get("haveExistIdcNum") + 1);
                    // 403
                    // Apollo中不存在该appId
                } else if ("403".equalsIgnoreCase(s)) {
                    log.error("没有找到appId,该appId is:{},调用Apollo返回信息:{}", appId, s);
                    System.out.println(s);
                    errorAppIdList.add(appId);
                    countMap.put("errorAppIdNum", countMap.get("errorAppIdNum") + 1);
                } else if ("302".equalsIgnoreCase(s)) {
                    log.error("cookie不正确或者已经失效,调用Apollo返回信息:{}", s);
                    return s;
                }
            }
        } catch (IOException e) {
            log.error("远程调用Apollo来创建idc时发生失败,appId is {},env is {}, clusterName:{}", appId, env, clusterName);
        }
        return s;
    }

    /**
     * 创建AppId
     *
     * @param request
     * @param baseUrl
     * @param cookie
     * @return
     */
    private String createAppId(CreateAppIdRequest request, String baseUrl, String cookie) {
        // http://apolload.dev.choicesaas.cn/apps

        Assert.hasLength(baseUrl, "apollo地址不能为空");
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

        String s = null;
        try {
            log.info("创建appId时的参数:{}", jsonString);
            CloseableHttpResponse response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HTTP_OK) {
                s = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.displayName());
                log.info("在创建appId时候，创建成功，调用Apollo返回信息:{}", s);
                System.out.println(s);
            } else {
                s = String.valueOf(response.getStatusLine().getStatusCode());
                if ("302".equalsIgnoreCase(s)) {
                    log.error("cookie不正确或者已经失效,调用Apollo返回信息:{}", s);
                    return s;
                } else {
                    log.error("在创建appId时候，创建失败，调用Apollo返回信息:{}", s);
                    System.out.println(s);
                }
            }
        } catch (IOException e) {
            log.error("远程调用Apollo来创建appId时发生失败,入参:{}", jsonString);
        }
        return s;
    }

    /**
     * 检查appId是否存在
     *
     * @param appId
     * @param baseUrl
     */
    private void checkAppId(String appId, String baseUrl, String cookie, List<String> errorAppIdList) throws IOException {
        // http://apolload.dev.choicesaas.cn/apps/demo

        Assert.hasLength(appId, "appId不能为空");
        String createIdcUrl = "/apps/" + appId;
        Assert.hasLength(baseUrl, "apollo地址不能为空");
        String uri = baseUrl.concat(createIdcUrl);

        CloseableHttpClient httpClient = HttpClients.custom()
            .setConnectionTimeToLive(6000, TimeUnit.MILLISECONDS).build();
        HttpGet httpGet = new HttpGet(uri);
        httpGet.setHeader("Content-Type", "application/json;charset=utf8");
        httpGet.addHeader("Cookie", cookie);

        CloseableHttpResponse response = httpClient.execute(httpGet);
        if (response.getStatusLine().getStatusCode() == HTTP_OK) {
            String s = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.displayName());
            log.info("CreateApolloIdcController checkAppId appId is:{}, result:{}", appId, s);
            System.out.println(s);
        } else {
            String s = String.valueOf(response.getStatusLine().getStatusCode());
            log.warn("CreateApolloIdcController checkAppId not exist appId is:{}, result:{}", appId, s);
            errorAppIdList.add(appId);
            System.out.println(s);
        }
    }

    /**
     * 获取Cookie
     *
     * @return
     */
    public String getCookie(String baseUrl) {
        Assert.hasLength(baseUrl, "地址不能为空");
        // 登陆 Url
        String loginUrl = baseUrl.concat("/signin");

        org.apache.commons.httpclient.HttpClient httpClient = new HttpClient();

        // 模拟登陆
        PostMethod postMethod = new PostMethod(loginUrl);

        // 设置登陆时要求的信息，用户名和密码
        NameValuePair[] data = {new NameValuePair("username", "apollo"), new NameValuePair("password", "d76ba0cd1b")};
        postMethod.setRequestBody(data);

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
                    System.out.println("cookies = " + c.toString());
                }
                System.out.println(Arrays.toString(cookies));
                return tmpCookies.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
