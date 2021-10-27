package com.choice.cloud.architect.groot.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/6 11:22
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@ApiModel("构建记录列表")
public class ListBuildRecordDTO extends BaseBizDTO{

    @ApiModelProperty(value = "发布oid")
    private String publishOid;

    @ApiModelProperty(value = "构建序号")
    private Integer buildNum;

    @ApiModelProperty(value = "构建结果")
    private String result;

    @ApiModelProperty(value = "构建描述")
    private String buildDesc;

    @ApiModelProperty(value = "应用编码")
    private String appCode;

    @ApiModelProperty(value = "jenkins job name")
    private String jobName;

    @ApiModelProperty(value = "job模板版本")
    private String version;

    @ApiModelProperty(value = "job模板版本")
    private Long duration;
}
