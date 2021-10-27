package com.choice.cloud.architect.groot.dto.workordersystem;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * <p>
 * 新增工单请求体
 * </p>
 *
 * @author LZ
 * @date Created in 2020/8/10 14:55
 */
@Data
public class CreateWorkOrderRequestDTO {
    /**
     * 工单名称
     */
    @NotBlank(message = "工单名称不能为空")
    private String orderName;
    /**
     * 工单类型编码
     */
    //@NotBlank(message = "工单类型编码不能为空")
    private String orderTypeCode;
    /**
     * 工单类型名称
     */
    @NotBlank(message = "工单类型名称不能为空")
    private String orderTypeName;
    /**
     * 工单归属环境(环境列表)
     */
    @NotNull(message = "工单归属环境不能为空")
    private List<String> envList;
    /**
     * 工单申请原因
     */
    @NotBlank(message = "工单申请原因不能为空")
    private String orderApplyReason;
}
