package com.choice.cloud.architect.groot.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * @author zhangkun
 */
@Data
@ToString(callSuper = true)
@ApiModel("IDC LDC 列表Model")
@Builder
public class ListIdcLdcDTO {
    /**
     * 机房分组id
     */
    @ApiModelProperty(value = "IDC")
    private String idc;
    /**
     * 机房分组id
     */
    @ApiModelProperty(value = "LDC")
    private String ldc;
    /**
     * 机房分组类型 （test：线下，pro：线上）
     */
    @ApiModelProperty(value = "env")
    private String env;
}
