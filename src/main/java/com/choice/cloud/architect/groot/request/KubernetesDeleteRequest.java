package com.choice.cloud.architect.groot.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel("Kubernetes删除")
public class KubernetesDeleteRequest {

    @ApiModelProperty(value = "名称",required = true)
    private String name;

    @ApiModelProperty(value = "标签名称",required = true)
    private String lableValue;

    @ApiModelProperty(value = "镜像",required = true)
    private String image;

    @ApiModelProperty(value = "端口",required = true)
    private Integer port;

    @ApiModelProperty(value = "标签名称？？",required = true)
    private String matchLable;


}
