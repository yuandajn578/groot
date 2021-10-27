package com.choice.cloud.architect.groot;

import com.choice.cloud.architect.groot.util.CommonUtil;

/**
 * @author zhangkun
 */
public class CommonUtilTest {
    public static void main(String[] args) {
//        System.out.println(CommonUtil.getUUIdUseMongo());
        String podName = "milky-way-5d95d98f6d-8h96q";
        String tempStr = podName.substring(0, podName.lastIndexOf("-"));
        String appCode = tempStr.substring(0, tempStr.lastIndexOf("-"));
        System.out.println(appCode);

    }
}
