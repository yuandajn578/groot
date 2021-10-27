package com.choice.cloud.architect.groot.dto;

import lombok.Data;

/**
 * @Author: zhangguoquan
 * @Date: 2020/9/10 15:06
 */
@Data
public class CompensateDTO {

    private String processInstanceId;

    private String EventType;

    private String processCode;

    private String type;

    private String result;
}
