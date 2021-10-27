package com.choice.cloud.architect.groot.dto.workordersystem;

import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * <p>
 * 主动关闭工单请求体
 * </p>
 *
 * @author LZ
 * @date Created in 2020/8/10 14:25
 */
@Data
public class ActiveCloseWorkOrderRequestDTO {
    /**
     * 工单编码（UUID）
     */
    @NotBlank(message = "工单编码不能为空")
    private String orderCode;
}
