package com.choice.cloud.architect.groot.dto.workordersystem;

import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * <p>
 * 接单请求体
 * </p>
 *
 * @author LZ
 * @date Created in 2020/8/10 23:32
 */
@Data
public class StartHandleWorkOrderRequestDTO {
    /**
     * 工单编码（UUID）
     */
    @NotBlank(message = "工单编码不能为空")
    private String orderCode;
}
