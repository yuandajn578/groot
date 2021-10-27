package com.choice.cloud.architect.groot.util;

/**
 * @author zhangkun
 */
public class NamespaceUtils {
    public static String getNamespace(String envType, String envCode) {
        return envType.concat("-").concat(envCode);
    }
}
