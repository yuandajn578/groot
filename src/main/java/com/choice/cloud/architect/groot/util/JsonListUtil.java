package com.choice.cloud.architect.groot.util;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

/**
 * <p>
 * List集合和JSON互转工具类
 * </p>
 *
 * @author LZ
 * @date Created in 2020/4/23 10:19
 */
public class JsonListUtil {
    /**
     * List<T> 转 json 保存到数据库
     */
    public static <T> String listToJson(List<T> ts) {
        String jsons = JSON.toJSONString(ts);
        return jsons;
    }

    /**
     * json 转 List<T>
     */
    public static <T> List<T> jsonToList(String jsonString, Class<T> clazz) {
        List<T> ts = JSONArray.parseArray(jsonString, clazz);
        return ts;
    }
}
