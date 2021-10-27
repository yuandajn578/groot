package com.choice.cloud.architect.groot.request;

import lombok.Data;
import lombok.ToString;

/**
 * @author zhangkun
 */
@Data
@ToString
public class AddChangeBindEnvRequest {
    private String changeId;
    private String appCode;
    private String envType;
    private String envCode;
}
