package com.choice.cloud.architect.groot.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;


@Getter
@Setter
@NoArgsConstructor
@ApiModel(description = "人员新增参数")
public class CreateMemberRequest {

    /** 变更单ID **/
    @ApiModelProperty(value = "变更单ID")
    private String changeId;
    /** 成员分类 **/
    @ApiModelProperty(value = "成员分类",required = true)
    @NotEmpty(message = "成员分类不能为空")
    private String memberType;
    /** 成员ID **/
    @ApiModelProperty(value = "成员ID",required = true)
    @NotEmpty(message = "成员ID不能为空")
    private String memberId;
    /** 成员名称 **/
    @ApiModelProperty(value = "成员名称",required = true)
    @NotEmpty(message = "成员名称不能为空")
    private String memberName;
    /** 联系方式 **/
    @ApiModelProperty(value = "联系方式",required = true)
    private String mobile;

    @Builder
    public CreateMemberRequest(String changeId, String memberType, String memberId, String memberName, String mobile) {
        this.changeId = changeId;
        this.memberType = memberType;
        this.memberId = memberId;
        this.memberName = memberName;
        this.mobile = mobile;
    }
}
