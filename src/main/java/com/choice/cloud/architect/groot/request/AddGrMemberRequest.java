package com.choice.cloud.architect.groot.request;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/27 14:13
 */
@Data
@ApiModel(description = "变更单关联用户")
public class AddGrMemberRequest {

    /** 变更单ID **/
    private String changeId;
    /** 成员分类 **/
    private String memberType;
    /** 成员ID **/
    private String memberId;
    /** 成员名称 **/
    private String memberName;
}
