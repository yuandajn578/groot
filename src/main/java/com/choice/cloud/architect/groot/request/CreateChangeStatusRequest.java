package com.choice.cloud.architect.groot.request;

import com.choice.cloud.architect.groot.enums.OperateEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "应用变更单参数")
public class CreateChangeStatusRequest extends CreateChangeRequest {

    @ApiModelProperty(value = "操作类型")
    private OperateEnum operateEnum;
}
