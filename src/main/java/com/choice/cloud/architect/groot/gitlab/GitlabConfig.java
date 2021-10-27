package com.choice.cloud.architect.groot.gitlab;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

/**
 * @ClassName GitlabConfig
 * @Description TODO
 * @Author Guangshan Wang
 * @Date 2020/3/16/016 22:13
 */
public class GitlabConfig {

    /**
     * gitlab访问地址
     */
    public static final String HOST_URL = "http://gitlab.choicesoft.com.cn/";
    /**
     * gitlab账号token
     */
    public static final String API_TOKEN = "zf7yxxVjcx_7gqyEDjZz";
    /**
     * HTTP 请求时，携带的请求头的key
     */
    public static final String HEADER_TOKEN_KEY = "PRIVATE-TOKEN";

    /**
     * gitlab账号id
     */
    public static final Integer USER_ID = 314;

}
