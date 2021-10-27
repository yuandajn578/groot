package com.choice.cloud.architect.groot.dto.workordersystem;

import java.util.Date;

import com.choice.cloud.architect.groot.enums.WorkOrderStatusEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * <p>
 * 工单分页列表查询响应体
 * </p>
 *
 * @author LZ
 * @date Created in 2020/8/10 11:13
 */
@Data
public class PageListWorkOrderResponseDTO {
    /**
     * 工单编码
     */
    private String orderCode;
    /**
     * 工单名称
     */
    private String orderName;
    /**
     * 工单类型编码
     */
    private String orderTypeCode;
    /**
     * 工单类型名称
     */
    private String orderTypeName;
    /**
     * 工单状态(待接单，已接单待处理，处理完成，已确认，已驳回，主动关闭)
     * {@link WorkOrderStatusEnum} 详细的工单状态枚举
     */
    private String orderStatus;
    /**
     * 工单归属环境(多个环境使用逗号隔开)
     */
    private String orderBelongEnv;
    /**
     * 工单申请人
     */
    private String orderApplyUser;
    /**
     * 工单申请原因
     */
    private String orderApplyReason;
    /**
     * 工单处理人
     */
    private String orderHandlerUser;
    /**
     * 工单处理反馈
     */
    private String orderHandlerFeedback;
    /**
     * 是否有全部按钮的权限
     */
    private Boolean havePermission = false;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "YYYY-MM-dd HH:mm:ss")
    private Date cTime;
}
