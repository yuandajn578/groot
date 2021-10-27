package com.choice.cloud.architect.groot.util;

import com.alibaba.fastjson.JSONArray;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * @author zhangkun
 */
public class JsonUtils {
    public static String jsonArrayString(List<String> jsonList) {
        if (CollectionUtils.isEmpty(jsonList)) {
            return null;
        }

        JSONArray jsonArray = new JSONArray();
        jsonArray.addAll(jsonList);
        return jsonArray.toJSONString();
    }
}
