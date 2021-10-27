package com.choice.cloud.architect.groot.dto.workordersystem;

import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * <p>
 * 创建工单类型请求体
 * </p>
 *
 * @author LZ
 * @date Created in 2020/8/10 16:07
 */
@Data
public class CreateWorkOrderTypeRequestDTO {
    /**
     * 工单类型编码
     */
    @NotBlank(message = "工单类型编码不能为空")
    private String orderTypeCode;
    /**
     * 工单类型名称
     */
    @NotBlank(message = "工单类型名称不能为空")
    private String orderTypeName;
    /**
     * 工单类型描述
     */
    private String orderTypeDesc;
    /**
     * 工单类型归属用户id
     */
    private String orderTypeBelongUserId;
    /**
     * 工单类型归属用户
     */
    private String orderTypeBelongUser;
}
