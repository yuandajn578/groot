package com.choice.cloud.architect.groot.support;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangkun
 */
public class PodNameParser {
    /**
     * Pod Name分隔符
     */
    private final static String SEPARATOR = "-";

    /**
     * Pod组成部分个数
     */
    private final static int POD_NAME_PARTS_NUM = 6;

    private List<String> podNameParts;

    public PodNameParser(String podName){
        List<String> list =
                Splitter.on(SEPARATOR).splitToList(podName);

        if (list.isEmpty() || list.size() < POD_NAME_PARTS_NUM) {
            throw new IllegalArgumentException("podName不合法");
        }

        this.podNameParts = list;
    }

    public String getAppName() {
        ArrayList<String> appNameList = new ArrayList<>(podNameParts.subList(0, podNameParts.size() - 5));

        return Joiner.on(SEPARATOR).join(appNameList);
    }

    public String getIdc() {
        return podNameParts.get(podNameParts.size() - 5);
    }

    public String getLdc() {
        return podNameParts.get(podNameParts.size() - 4);
    }

    public String getServiceGroup() {
        return podNameParts.get(podNameParts.size() - 3);
    }

    public String getNumFlag() {
        return podNameParts.get(podNameParts.size() - 2);
    }
}
