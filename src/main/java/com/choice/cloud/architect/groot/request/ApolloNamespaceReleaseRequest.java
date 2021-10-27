package com.choice.cloud.architect.groot.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@ApiModel(description = "阿波罗发布信息")
public class ApolloNamespaceReleaseRequest {

    @ApiModelProperty(value = "此次发布的标题")
    private String releaseTitle;
    @ApiModelProperty(value = "发布的备注")
    private String releaseComment;
    @ApiModelProperty(value = "发布人")
    private String releasedBy;
    @ApiModelProperty(value = "***")
    private boolean isEmergencyPublish;

    @Builder
    public ApolloNamespaceReleaseRequest(String releaseTitle, String releaseComment, String releasedBy, boolean isEmergencyPublish) {
        this.releaseTitle = releaseTitle;
        this.releaseComment = releaseComment;
        this.releasedBy = releasedBy;
        this.isEmergencyPublish = isEmergencyPublish;
    }
}
