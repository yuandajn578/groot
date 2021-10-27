package com.choice.cloud.architect.groot;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * @author zhangkun
 */
public class LocalDateTimeTest {
    public static void main(String[] args) {
        LocalDateTime localDateTime = LocalDateTime.parse("2020-07-20T08:45:09Z", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
        System.out.println(localDateTime.plusHours(8L).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
}
