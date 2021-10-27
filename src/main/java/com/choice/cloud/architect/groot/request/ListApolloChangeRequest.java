package com.choice.cloud.architect.groot.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@ApiModel(description = "阿波罗变更单分页查询参数")
@Getter
@Setter
public class ListApolloChangeRequest extends PageRequest {

    @ApiModelProperty(value = "环境名",required = true)
    @NotEmpty(message = "环境名不能为空")
    private String env;

    @ApiModelProperty(value = "应用名",required = true)
    @NotEmpty(message = "应用名不能为空")
    private String appCode;

    @ApiModelProperty(value = "集群名称")
    @NotEmpty(message = "集群名称不能为空")
    private String cluster;

    @ApiModelProperty(value = "apollo变更子项key值")
    private String changeItemKey;
    /**
     * 变更单编码
     */
    @ApiModelProperty(value = "变更单编码")
    private String changeId;

}
