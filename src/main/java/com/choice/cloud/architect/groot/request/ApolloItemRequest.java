package com.choice.cloud.architect.groot.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@ApiModel(description = "阿波罗配置项信息")
public class ApolloItemRequest  extends ApolloBaseRequest{

    @ApiModelProperty(value = "配置项key")
    @NotEmpty(message = "配置项key不能为空")
    private String key;

    @ApiModelProperty(value = "配置项值")
    @NotEmpty(message = "配置项值不能为空")
    @Size(max = 2000, message = "配置值不能超过2000")
    private String value;

    @ApiModelProperty(value = "配置项注释")
    private String comment;

    @Builder
    public ApolloItemRequest(String dataChangeCreatedBy, String dataChangeLastModifiedBy, Date dataChangeCreatedTime, Date dataChangeLastModifiedTime, String key, String value, String comment) {
        super(dataChangeCreatedBy, dataChangeLastModifiedBy, dataChangeCreatedTime, dataChangeLastModifiedTime);
        this.key = key;
        this.value = value;
        this.comment = comment;
    }
}
