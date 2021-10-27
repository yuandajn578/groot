package com.choice.cloud.architect.groot.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/13 18:49
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class GrChangeLifeCircle extends BaseBizModel{

    private String changeId;

    private String changeName;

    private String project;

    private String appCode;

    private String appName;

    private String envType;

    private String envCode;

    private String envName;

    private String operateType;

    private String operator;

    private LocalDateTime operateTime;

    private String operateResult;

    private String relOrderType;

    private String relOrderId;

}
