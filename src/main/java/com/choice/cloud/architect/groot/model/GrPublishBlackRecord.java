package com.choice.cloud.architect.groot.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/21 16:22
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class GrPublishBlackRecord extends BaseBizModel{

    private String blackType;

    private int weekCode;

    private LocalTime beginTime;

    private LocalTime endTime;

    private LocalDate blackDate;
}
