package com.choice.cloud.architect.groot.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApolloBaseRequest {

    @ApiModelProperty(value = "修改人")
    protected String dataChangeCreatedBy;

    @ApiModelProperty(value = "最后修改人")
    protected String dataChangeLastModifiedBy;

    @ApiModelProperty(value = "创建时间")
    protected Date dataChangeCreatedTime;

    @ApiModelProperty(value = "最后修改时间")
    protected Date dataChangeLastModifiedTime;

}
