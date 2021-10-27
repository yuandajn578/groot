package com.choice.cloud.architect.groot.request;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.List;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/25 18:11
 */
@Data
@ApiModel(description = "添加日期黑名单")
public class AddDateBlackRecordRequest {

    @NotEmpty(message = "日期列表不能为空")
    List<LocalDate> dateList;
}
