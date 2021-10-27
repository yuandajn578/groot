package com.choice.cloud.architect.groot.dto.workordersystem;

import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * <p>
 * 处理完成工单请求体
 * </p>
 *
 * @author LZ
 * @date Created in 2020/8/10 13:27
 */
@Data
public class HandleFinishWorkOrderRequestDTO {
    /**
     * 工单编码（UUID）
     */
    @NotBlank(message = "工单编码不能为空")
    private String orderCode;
    /**
     * 工单处理反馈
     */
    @NotBlank(message = "工单处理反馈不能为空")
    private String orderHandlerFeedback;
}
