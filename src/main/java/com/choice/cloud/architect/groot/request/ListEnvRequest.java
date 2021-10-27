package com.choice.cloud.architect.groot.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * <p>
 * 应用环境列表查询入参
 * </p>
 *
 * @author zhangkun
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@ApiModel("环境列表分页查询Model")
public class ListEnvRequest extends PageRequest {
    @ApiModelProperty(value = "环境名称")
    private String envName;

    @ApiModelProperty(value = "环境名称")
    private String envType;
}
