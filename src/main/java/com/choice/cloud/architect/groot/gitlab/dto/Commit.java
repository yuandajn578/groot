package com.choice.cloud.architect.groot.gitlab.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * @ClassName Commit
 * @Description TODO
 * @Author Guangshan Wang
 * @Date 2020/6/24/024 15:00
 */
@Data
public class Commit {
    String id;
    @JSONField(name = "short_id")
    String shortId;
    String title;
    @JSONField(name = "short_id")
    String authorName;
    @JSONField(name = "author_email")
    String authorEmail;
    @JSONField(name = "authored_date")
    String authoredDate;
    @JSONField(name = "committer_name")
    String committerName;
    @JSONField(name = "committer_email")
    String committerEmail;
    @JSONField(name = "committed_date")
    String committedDate;
    @JSONField(name = "created_at")
    String createdAt;
    String message;
    @JSONField(name = "parent_ids")
    List<String> parentIds;
    @JSONField(name = "web_url")
    String webUrl;
}
