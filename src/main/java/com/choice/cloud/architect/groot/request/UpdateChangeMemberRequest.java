package com.choice.cloud.architect.groot.request;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * @Author: zhangguoquan
 * @Date: 2020/4/1 16:40
 */
@Data
@ApiModel(description = "更新变更单关联用户")
public class UpdateChangeMemberRequest {

    /** 变更单ID **/
    private String changeId;
    /** 成员分类 **/
    private String memberType;

    private List<AddGrMemberRequest> memberList;

}
