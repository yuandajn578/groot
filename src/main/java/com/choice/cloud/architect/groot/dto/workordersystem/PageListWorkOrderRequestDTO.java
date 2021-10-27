package com.choice.cloud.architect.groot.dto.workordersystem;

import com.choice.cloud.architect.groot.dto.PageRequestBaseDTO;
import com.choice.cloud.architect.groot.enums.WorkOrderStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 工单分页列表查询请求体
 * </p>
 *
 * @author LZ
 * @date Created in 2020/8/10 11:14
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PageListWorkOrderRequestDTO extends PageRequestBaseDTO {

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
     * {@link WorkOrderStatusEnum} 完整工单状态
     */
    private String orderStatus;
    /**
     * 工单申请人
     */
    private String orderApplyUser;
    /**
     * 工单处理人
     */
    private String orderHandlerUser;
}
