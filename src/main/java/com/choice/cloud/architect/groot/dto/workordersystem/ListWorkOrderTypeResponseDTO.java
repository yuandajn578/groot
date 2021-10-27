package com.choice.cloud.architect.groot.dto.workordersystem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 工单类型列表响应体
 * </p>
 *
 * @author LZ
 * @date Created in 2020/8/10 15:55
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ListWorkOrderTypeResponseDTO {
    /**
     * 工单类型编码
     */
    private String orderTypeCode;
    /**
     * 工单类型名称
     */
    private String orderTypeName;
}
