package com.choice.cloud.architect.groot.util;

import java.util.UUID;

/**
 * <p>
 * 生成唯一id
 * </p>
 *
 * @author LZ
 * @date Created in 2020/4/24 9:41
 */
public class UUIDUtil {
    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
