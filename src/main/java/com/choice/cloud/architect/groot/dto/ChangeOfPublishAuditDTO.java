package com.choice.cloud.architect.groot.dto;

import com.choice.cloud.architect.groot.model.GrMember;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author zhangkun
 */
@Data
@ToString
public class ChangeOfPublishAuditDTO extends BaseBizDTO{
    /**
     * id
     */
    private String oid;
    /**
     * 变更名称
     */
    private String changeName;
    /**
     * appCode
     */
    private String appCode;
    /**
     * 中文应用名称(银河)
     */
    private String appName;

    /**
     * 研发人员
     */
    private String developer;

    /**
     * 测试人员
     */
    private String tester;

    /**
     * apollo env
     */
    private String apolloEnv;

    /**
     * apollo idc
     */
    private String apolloIdc;

    /**
     * gitlab repo
     */
    private String gitRepo;

    /**
     * gitlab branch
     */
    private String gitBranch;


    /**
     * 描述信息
     */
    private String description;

    /**
     * cr人员列表
     */
    private List<GrMember> crMembers;
}
