package com.choice.cloud.architect.groot.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotEmpty;

@ApiModel(description = "修改变更单阶段参数")
@Data
@NoArgsConstructor
public class UpdateChangeStageRequest {

    @ApiModelProperty(value = "变更单ID",required = true)
    @NotEmpty(message = "变更单ID不能为空")
    private String oid;

    @ApiModelProperty(value = "变更单阶段 DEV(开发) TEST(测试) PRE(预发) PROD(生产)",required = true)
    @NotEmpty(message = "变更单阶段不能为空")
    private String stage;

    private Integer status;

    @Builder
    public UpdateChangeStageRequest(String oid,String stage) {
        this.oid = oid;
        this.stage = stage;
    }
}
