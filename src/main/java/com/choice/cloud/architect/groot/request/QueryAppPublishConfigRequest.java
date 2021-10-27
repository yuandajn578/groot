package com.choice.cloud.architect.groot.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

/**
 * @author zhangkun
 */
@ApiModel(value = "查询指定应用指定环境分组下的发布配置")
@Data
@ToString
public class QueryAppPublishConfigRequest {
    /**
     * 应用编码
     */
    @NotEmpty
    @ApiModelProperty(value = "应用编码")
    private String appCode;

    /**
     * 环境类型
     */
    @NotEmpty
    @ApiModelProperty(value = "环境类型")
    private String envType;

    /**
     * 环境编码
     */
    @NotEmpty
    @ApiModelProperty(value = "环境编码")
    private String envCode;
}
