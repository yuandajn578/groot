package com.choice.cloud.architect.groot.request;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

/**
 * @author zhangkun
 */
@Data
@ToString
public class DeploymentRollbackPreviousRequest {
    @NotEmpty
    private String envType;
    @NotEmpty
    private String envCode;
    @NotEmpty
    private String appCode;
}
