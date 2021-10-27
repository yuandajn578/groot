package com.choice.cloud.architect.groot.support;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

/**
 * @author zhangkun
 */
public class IdcLdcHolder {
    private static List<String> offlineIdc = Lists.newArrayList();
    private static List<String> onlineIdc = Lists.newArrayList();

    public static List<String> getOfflineIdc() {
        return Collections.unmodifiableList(offlineIdc);
    }

    public static List<String> getOnlineIdc() {
        return Collections.unmodifiableList(onlineIdc);
    }

    public static void setOfflineIdc(List<String> offlineIdc) {
        IdcLdcHolder.offlineIdc = offlineIdc;
    }

    public static void setOnlineIdc(List<String> onlineIdc) {
        IdcLdcHolder.onlineIdc = onlineIdc;
    }
}
