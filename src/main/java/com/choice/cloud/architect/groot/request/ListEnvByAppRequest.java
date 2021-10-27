package com.choice.cloud.architect.groot.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

/**
 * @author zhangkun
 */
@ApiModel(value = "根据appCode查询环境信息Model")
@Data
@ToString
public class ListEnvByAppRequest {
    @ApiModelProperty(value = "应用编码")
    @NotEmpty
    private String appCode;
}
