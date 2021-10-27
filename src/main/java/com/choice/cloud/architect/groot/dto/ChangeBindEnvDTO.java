package com.choice.cloud.architect.groot.dto;

import lombok.Data;
import lombok.ToString;

/**
 * @author zhangkun
 */
@Data
@ToString
public class ChangeBindEnvDTO {
    private String changeId;
    private String envType;
    private String envCode;
    private String envName;
}
