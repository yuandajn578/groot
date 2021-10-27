package com.choice.cloud.architect.groot.support;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author yuxiaopeng
 * @Description 容器名称生成处理器
 * @Date 10:44 2020/6/10
 * @Param
 * @return
 **/
@Slf4j
public class PodNameCreateHandler {
    /**
     * Pod Name分隔符
     */
    private final static String DELIMITER = "-";

    /**
     * 单个LDC下最大的Pod个数
     */
    private static final int DEFAULT_MAX_POD_PER_LDC = 64;

    public static String handler(String appName, String xServiceGroup, String idc, String ldc, int randomMax, int podNum) {
        List<String> podNameList = new ArrayList<>();
        if (StringUtils.isEmpty(appName) || StringUtils.isEmpty(xServiceGroup) ||
                StringUtils.isEmpty(idc) || StringUtils.isEmpty(ldc)) {
            throw new IllegalArgumentException("参数异常");
        }

        int intLdc = Integer.parseInt(ldc.substring(3));
        if (intLdc < 0 || randomMax + podNum > DEFAULT_MAX_POD_PER_LDC * intLdc) {
            log.info("参数异常,idc:{},ldc:{},randomMax:{},podNum:{}", idc, ldc, randomMax, podNum);
            return String.join(",", podNameList);
        }

        while (podNum > 0) {
            String podName = appName + DELIMITER
                    + idc + DELIMITER
                    + ldc + DELIMITER
                    + xServiceGroup + DELIMITER
                    + String.format("%04d", (randomMax + podNum)) + DELIMITER
                    + "0";
            podNameList.add(podName);
            podNum--;
        }

        return String.join(",", podNameList);
    }

    private PodNameCreateHandler(){}
}
