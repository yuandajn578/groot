package com.choice.cloud.architect.groot.request;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author zhangkun
 */
@ApiOperation(value = "删除StatefulSet资源")
@Data
@ToString
public class ScaleUpPodRequest {
    @ApiModelProperty(value = "环境类型")
    @NotEmpty
    private String envType;

    @ApiModelProperty(value = "环境编码")
    @NotEmpty
    private String envCode;

    @ApiModelProperty(value = "应用编码")
    @NotEmpty
    private String appCode;

    @ApiModelProperty(value = "idc")
    @NotEmpty
    private String idc;

    @ApiModelProperty(value = "ldc")
    @NotEmpty
    private String ldc;

    @ApiModelProperty(value = "扩容节点个数")
    @NotNull
    private Integer scaleUpNum;
}
