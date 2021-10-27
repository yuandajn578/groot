package com.choice.cloud.architect.groot.dto;

import lombok.Data;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/5 9:33
 */

@Data
public class BuildResultDTO {

    private String jobName;

    private Integer buildNum;

    private String buildId;

    private String publishOid;

    private String code = "0";

    private String message;
}
