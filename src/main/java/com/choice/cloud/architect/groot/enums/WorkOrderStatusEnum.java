package com.choice.cloud.architect.groot.enums;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

/**
 * <p>
 * 工单状态枚举类
 * </p>
 *
 * @author LZ
 * @date Created in 2020/8/10 13:51
 */
@Slf4j
@Getter
public enum WorkOrderStatusEnum {
    // 待接单，已接单待处理，处理完成，已确认，已驳回，主动关闭
    ORDER_WAITING("order_waiting", "待接单"),
    ORDER_WAITING_HANDLE("order_waiting_handle", "已接单待处理"),
    ORDER_FINISH("order_finish", "处理完成"),
    ORDER_HAVE_CONFIRMED("order_have_confirmed", "已确认"),
    ORDER_REJECT("order_reject", "已驳回"),
    ORDER_ACTIVE_CLOSE("order_active_close", "主动关闭"),
    ;

    WorkOrderStatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    private String code;
    private String name;

    /**
     * 根据工单编码获取工单状态名字
     * @param code
     * @return
     */
    public static String getNameByCode(String code) {
        Assert.hasLength(code, "工单编码不能为空");
        if (code.equalsIgnoreCase(ORDER_WAITING.getCode())) {
            return ORDER_WAITING.getName();
        } else if (code.equalsIgnoreCase(ORDER_WAITING_HANDLE.getCode())) {
            return ORDER_WAITING_HANDLE.getName();
        } else if (code.equalsIgnoreCase(ORDER_FINISH.getCode())) {
            return ORDER_FINISH.getName();
        } else if (code.equalsIgnoreCase(ORDER_HAVE_CONFIRMED.getCode())) {
            return ORDER_HAVE_CONFIRMED.getName();
        } else if (code.equalsIgnoreCase(ORDER_REJECT.getCode())) {
            return ORDER_REJECT.getName();
        } else if (code.equalsIgnoreCase(ORDER_ACTIVE_CLOSE.getCode())) {
            return ORDER_ACTIVE_CLOSE.getName();
        } else {
            log.error("根据工单编码没有找到对应的工单状态名称");
            return "";
        }
    }
}
