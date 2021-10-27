package com.choice.cloud.architect.groot.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@ApiModel(description = "阿波罗变更单-创建或修改")
public class UpdateApolloChangeRequest {

    @ApiModelProperty(value = "阿波罗变更单主键",required = true)
    //@NotEmpty(message = "阿波罗变更单ID不能为空")
    private String oid;

    @ApiModelProperty(value = "阿波罗应用ID",required = true)
    @NotEmpty(message = "应用ID不能为空")
    private String apolloAppCode;

    @ApiModelProperty(value = "环境名称",required = true)
    @NotEmpty(message = "环境名称不能为空")
    private String env;

    @ApiModelProperty(value = "集群名称",required = true)
    @NotEmpty(message = "集群名称不能为空")
    private String cluster;

    /**
     * 变更单编码
     */
    @ApiModelProperty(value = "变更单编码 ,新增的时候必传")
    private String changeId;

    /**
     * apollo变更子项key值
     */
    @ApiModelProperty(value = "apollo变更子项key值",required = true)
    @NotEmpty(message = "apollo变更子项key值不能为空")
    private String changeItemKey;

    /**
     * apollo变更子项value值
     */
    @ApiModelProperty(value = "apollo变更子项value值",required = true)
    @NotEmpty(message = "apollo变更子项value值不能为空")
    private String changeItemValue;

    /**
     * 描述信息
     */
    @ApiModelProperty(value = "描述信息",required = true)
    //@NotEmpty(message = "描述信息不能为空")
    private String description;


}
