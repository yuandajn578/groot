package com.choice.cloud.architect.groot.remote.milkyway;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@ApiModel(description = "架构与参数")
public class DomainArchitectureParamId {

    @ApiModelProperty(value = "架构域id")
    private String oid;

}
