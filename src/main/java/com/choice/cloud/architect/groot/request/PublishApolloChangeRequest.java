package com.choice.cloud.architect.groot.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@ApiModel(description = "阿波罗变更单-删除")
public class PublishApolloChangeRequest {

    @ApiModelProperty(value = "阿波罗应用ID",required = true)
    @NotEmpty(message = "应用ID不能为空")
    private String apolloAppCode;

    @ApiModelProperty(value = "环境名称",required = true)
    @NotEmpty(message = "环境名称不能为空")
    private String env;

    @ApiModelProperty(value = "集群名称",required = true)
    @NotEmpty(message = "集群名称不能为空")
    private String cluster;

    @ApiModelProperty(value = "发布标题",required = true)
    @NotEmpty(message = "发布标题")
    private String releaseTitle;

    @ApiModelProperty(value = "集群名称")
    private String releaseComment;

    @ApiModelProperty(value = "变更单编码",position = 3,required = true)
    private String changeId;


}
