package com.choice.cloud.architect.groot.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author zhangkun
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class GrPublishAuditChangeRel extends BaseBizModel{
    /**
     * 变更单id
     */
    private String changeId;

    /**
     * 发布审核单id
     */
    private String publishAuditId;
}
