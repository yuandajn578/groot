package com.choice.cloud.architect.groot.util;

import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 对象POJO和JSON互转
 * </p>
 *
 * @author LZ
 * @date Created in 2020/4/23 10:17
 */
@Slf4j
public class JsonUtil {
    /**
     * JSON 转 POJO
     */
    public static <T> T getObject(String pojo, Class<T> targetClass) {
        try {
            return JSONObject.parseObject(pojo, targetClass);
        } catch (Exception e) {
            log.error(targetClass + "转 JSON 失败");
        }
        return null;
    }

    /**
     * POJO 转 JSON
     */
    public static <T> String getJson(T tResponse) {
        String pojo = JSONObject.toJSONString(tResponse);
        return pojo;
    }
}
