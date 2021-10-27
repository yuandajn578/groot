package com.choice.cloud.architect.groot.dto;

import lombok.Data;

/**
 * @Author: zhangguoquan
 * @Date: 2020/4/16 14:36
 */
@Data
public class StatisticsBoxDTO {

    private Integer coreAppCount;

    private Integer commonAppCount;

    private Integer runningCount;

    private Integer publishingCount;

    private Integer publishSuccessCount;

    private Integer publishFailureCount;


}
