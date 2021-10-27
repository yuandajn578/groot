package com.choice.cloud.architect.groot.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

/**
 * @author zhangkun
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class QueryEnvBindChangeRequest extends PageRequest{
    @NotEmpty
    private String envType;
    @NotEmpty
    private String envCode;
    private String changeId;
}
