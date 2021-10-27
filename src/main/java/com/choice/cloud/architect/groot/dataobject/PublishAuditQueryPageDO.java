package com.choice.cloud.architect.groot.dataobject;

import lombok.Data;
import lombok.ToString;

/**
 * @author zhangkun
 */
@Data
@ToString
public class PublishAuditQueryPageDO {
    private String status;

    private String excludeEnvType;

    private String envType;

    private String searchValue;

}
