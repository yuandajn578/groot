package com.choice.cloud.architect.groot.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;

@Data
@ToString(callSuper = true)
@ApiModel("构建节点model")
public class JobBuildNodeDTO {

    @ApiModelProperty(value = "构建节点名称")
    private String displayName;

    @ApiModelProperty(value = "构建节点时长(ms)")
    private Integer durationInMillis;

    @ApiModelProperty(value = "构建节点id")
    private String id;

    @ApiModelProperty(value = "构建节点结果")
    private String result;

    @ApiModelProperty(value = "构建节点开始时间")
    private String startTime;

    @ApiModelProperty(value = "构建节点状态")
    private String state;

    @ApiModelProperty(value = "构建节点类型")
    private String type;

}
