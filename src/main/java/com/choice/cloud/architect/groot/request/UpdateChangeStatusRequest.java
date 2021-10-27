package com.choice.cloud.architect.groot.request;

import com.choice.cloud.architect.groot.enums.ChangeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@ApiModel(description = "变更单修改状态")
@Getter
@Setter
public class UpdateChangeStatusRequest {

    @ApiModelProperty(value = "变更单ID",required = true)
    @NotEmpty(message = "变更单ID不能为空")
    private String oid;

    /**
     * 变更单状态
     * 详见状态枚举 {@link ChangeEnum}
     */
    @ApiModelProperty(value = "变更单状态 0:进行中 1:待发布 2：已上线 3：已关闭",required = true,example = "4")
    @NotEmpty(message = "变更单状态不能为空")
    private Integer status;
}
