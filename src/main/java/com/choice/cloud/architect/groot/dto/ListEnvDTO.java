package com.choice.cloud.architect.groot.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author zhangkun
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@ApiModel("环境列表Model")
public class ListEnvDTO extends BaseBizDTO{
    /**
     * 环境中文描述
     */
    @ApiModelProperty(value = "环境名称")
    private String envName;

    /**
     * 环境编码
     */
    @ApiModelProperty(value = "环境编码")
    private String envCode;

    /**
     * 环境类型（0：开发，1：测试，2：正式）
     */
    @ApiModelProperty(value = "环境类型")
    private String envType;

    /**
     * 描述信息
     */
    @ApiModelProperty(value = "备注")
    private String description;
}
