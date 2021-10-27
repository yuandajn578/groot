package com.choice.cloud.architect.groot.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@ApiModel(description = "阿波罗变更单-日志")
public class ChangeRecodeRequest extends PageRequest  {

    /** 变更单ID **/
    @ApiModelProperty(value = "变更单ID")
    private String changeId;
}
