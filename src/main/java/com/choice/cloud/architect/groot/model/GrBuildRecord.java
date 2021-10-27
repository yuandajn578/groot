package com.choice.cloud.architect.groot.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/6 10:05
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class GrBuildRecord extends BaseBizModel{

    private String publishOid;

    private int buildNum;

    private String result;

    private String buildDesc;

    private String appCode;

    private String jobName;

    private String version;

    private Long duration;
}
