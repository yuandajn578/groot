package com.choice.cloud.architect.groot.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * @author zhangkun
 */
@Data
@ToString
@ApiModel(value = "修改环境状态")
public class ChangeEnvStatusRequest {
    @ApiModelProperty(value = "环境id，多个id以逗号分隔")
    @NotNull
    private String ids;
    @ApiModelProperty(value = "环境当前状态")
    @NotNull
    private Integer oldStatus;
    @ApiModelProperty(value = "环境修改状态")
    @NotNull
    private Integer expectStatus;
}
