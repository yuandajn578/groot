package com.choice.cloud.architect.groot.gitlab.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @ClassName MergeRequest
 * @Description 合并请求的实体，有一部分字段没有获取，后续使用的话，可以再增加
 * @Author Guangshan Wang
 * @Date 2020/6/24/024 17:38
 */
@Data
public class MergeRequest {

    private Integer id;
    private Integer iid;
    @JSONField(name = "project_id")
    private Integer projectId;
    private String title;
    private String description;
    private String state;
    @JSONField(name = "created_at")
    private String createdAt;
    @JSONField(name = "updated_at")
    private String updatedAt;
    @JSONField(name = "target_branch")
    private String targetBranch;
    @JSONField(name = "source_branch")
    private String sourceBranch;
    private Integer upvotes;
    private Integer downvotes;
    @JSONField(name = "source_project_id")
    private Integer sourceProjectId;
    @JSONField(name = "target_project_id")
    private Integer targetProjectId;
    @JSONField(name = "merge_status")
    private String mergeStatus;
    private String sha;
    @JSONField(name = "merge_commit_sha")
    private String mergeCommitSha;
    @JSONField(name = "web_url")
    private String webUrl;

}
