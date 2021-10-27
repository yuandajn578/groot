package com.choice.cloud.architect.groot.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@ApiModel(description = "阿波罗命名空间信息")
public class ApolloAppNamespaceRequest extends ApolloBaseRequest {

    @ApiModelProperty(value = "命名空间名称")
    @NotEmpty(message = "命名空间名称不能为空")
    private String name;

    @ApiModelProperty(value = "应用ID")
    @NotEmpty(message = "应用ID不能为空")
    private String appId;

    @ApiModelProperty(value = "格式")
    private String format;

    @ApiModelProperty(value = "是否发布")
    private boolean isPublic;

    @ApiModelProperty(value = "注释")
    private String comment;

    @Builder
    public ApolloAppNamespaceRequest(String dataChangeCreatedBy, String dataChangeLastModifiedBy, Date dataChangeCreatedTime, Date dataChangeLastModifiedTime, String name, String appId, String format, boolean isPublic, String comment) {
        super(dataChangeCreatedBy, dataChangeLastModifiedBy, dataChangeCreatedTime, dataChangeLastModifiedTime);
        this.name = name;
        this.appId = appId;
        this.format = format;
        this.isPublic = isPublic;
        this.comment = comment;
    }
}
