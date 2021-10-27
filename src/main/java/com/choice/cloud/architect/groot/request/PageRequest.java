package com.choice.cloud.architect.groot.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author zhangkun
 */
@Data
public class PageRequest {
    @ApiModelProperty(value = "当前页",required = true)
    @NotNull(message = "pageNum不能为null")
    private Integer pageNum;
    @ApiModelProperty(value = "每页显示条数",required = true)
    @NotNull(message = "pageSize不能为null")
    private Integer pageSize;
}
