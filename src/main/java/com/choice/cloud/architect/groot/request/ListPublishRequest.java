package com.choice.cloud.architect.groot.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/3 17:19
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@ApiModel("发布分页查询请求")
public class ListPublishRequest extends PageRequest {

    @ApiModelProperty(value = "应用编码")
    private String appCode;

    @ApiModelProperty(value = "环境类型（dev、test、pre、prod）")
    private String envType;

    @ApiModelProperty(value = "服务分组名")
    private String envCode;

    @ApiModelProperty(value = "变更单id")
    private String changeId;

    @ApiModelProperty(value = "查询参数")
    private String searchValue;
}
