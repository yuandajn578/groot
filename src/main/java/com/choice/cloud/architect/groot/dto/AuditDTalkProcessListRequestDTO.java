package com.choice.cloud.architect.groot.dto;

import lombok.Data;

/**
 * <p>
 * 审核单对应的钉钉审批流进度请求体
 * </p>
 *
 * @author LZ
 * @date Created in 2020/6/10 14:19
 */
@Data
public class AuditDTalkProcessListRequestDTO {
    private String dtalkProcessId;
}
