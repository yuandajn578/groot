package com.choice.cloud.architect.groot.response.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChangeResponseCode implements ResponseInfo   {

    NO_HAVE_BRANCH("39980","分支不存在"),
    STATUS_PUBLISHING_ERROR("39981","只有“进行中”的变更才可提交待发布"),
    STATUS_PUBLISH_ERROR("39982","只有“待发布”状态的变更才可发布"),
    PRE_STAGE_ERROR("39983","变更未进入预发阶段，不能发布预发环境"),
    PRO_PUBLISH_ERROR("39984","预发环境最后一次发布失败，不能发布生产"),
    PRE_TEST_ERROR("39984","预发环境最后一次发布失败，不能通过测试"),
    PRE_TEST_NOT_PUBLISH_ERROR("39985","预发环境没有发布过，不能通过测试"),
    PRO_STAGE_ERROR("39986","变更未进入生产阶段，不能发布生产环境"),
    CR_DEV_MEMBER_SAME("39986","单个CR人员不能与开发人员相同"),
    ;

    /** 状态码 */
    private final String code;

    /** 描述信息 */
    private final String desc;

}
