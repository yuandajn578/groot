package com.choice.cloud.architect.groot.request;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalTime;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/25 17:51
 */
@Data
@ApiModel(description = "添加星期黑名单")
public class AddWeekBlackRecordRequest {

    @NotNull(message = "星期不能为空")
    private Integer weekCode;

    @NotNull(message = "起始时间不能为空")
    private LocalTime beginTime;

    @NotNull(message = "终止时间不能为空")
    private LocalTime endTime;
}
