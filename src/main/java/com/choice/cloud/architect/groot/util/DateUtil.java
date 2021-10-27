package com.choice.cloud.architect.groot.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>
 * 日期工具类
 * </p>
 *
 * @author LZ
 * @date Created in 2020/6/10 14:40
 */
public class DateUtil {
    public static final String FORMAT = "YYYY-MM-dd HH:mm:ss";

    /**
     * 时间戳转换成日期格式字符串
     *
     * @param timeStamp Long类型的时间戳
     * @return
     */
    public static String timeStamp2Date(Long timeStamp) {
        if (timeStamp == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT);
        return sdf.format(new Date(timeStamp));
    }
}
