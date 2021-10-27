package com.choice.cloud.architect.groot.dataobject;

import lombok.Data;
import lombok.ToString;

/**
 * @author zhangkun
 */
@Data
@ToString
public class UnbindPublishAuditChangeRelDO {
    private String changeId;
    private String publishAuditId;
    private String updateUser;
    private Integer deleteFlag;
}
