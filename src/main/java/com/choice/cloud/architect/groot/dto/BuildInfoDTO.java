package com.choice.cloud.architect.groot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/5 9:42
 */
@Data
@ApiModel("构建信息model")
public class BuildInfoDTO {

    @ApiModelProperty(value = "构建id（编号）")
    private String buildId;

    @ApiModelProperty(value = "构建编号")
    private Integer buildNum;

    @ApiModelProperty(value = "job名称")
    private String jobName;

    @ApiModelProperty(value = "是否正在构建")
    private Boolean building;

    @ApiModelProperty(value = "git分支")
    private String gitBranch;

    @ApiModelProperty(value = "app名称")
    private String appName;

    @ApiModelProperty(value = "产出文件")
    private String product;

    @ApiModelProperty(value = "apollo环境")
    private String apolloEnv;

    @ApiModelProperty(value = "apollo idc")
    private String apolloIdc;

    @ApiModelProperty(value = "构建描述")
    private String desc;

    @ApiModelProperty(value = "环境名称")
    private String envName;

    @ApiModelProperty(value = "构建时长(毫秒)")
    private Long duration;

    @ApiModelProperty(value = "制品")
    private String artifacts;

    @ApiModelProperty(value = "Jenkins模板版本")
    private String templateVersion;

    @ApiModelProperty(value = "构建时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime buildTime;

    @ApiModelProperty(value = "构建节点信息")
    private List<JobBuildNodeDTO> nodes;
}
