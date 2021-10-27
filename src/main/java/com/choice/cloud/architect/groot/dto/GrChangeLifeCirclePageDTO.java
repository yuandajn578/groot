package com.choice.cloud.architect.groot.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/14 10:23
 */
@Data
@ToString
@ApiModel("变更生命周期")
public class GrChangeLifeCirclePageDTO extends BaseBizDTO{

    private String changeId;

    private String changeName;

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
