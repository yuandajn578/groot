package com.choice.cloud.architect.groot.response.code;

/**
 * <p>
 * 运维工单相关响应code
 * </p>
 */
public enum WorkOrderResponseCode implements ResponseInfo {
    /**
     *运维工单相关响应code
     */
    NOT_REJECT("90000", "当前工单状态不能处理拒绝操作"),
    NOT_CONFIRM("90001", "当前工单状态不能确认操作"),
    NOT_YOU_NOT_CONFIRM("90002", "该工单发起人不是你，不能确认该工单"),
    NOT_CLOSE("90004", "当前工单状态不能主动关闭操作"),
    NOT_YOU_NOT_CLOSE("90005", "该工单发起人不是你，不能关闭该工单"),
    NOT_ORDER("90006", "当前工单状态不能接单操作"),
    NOT_FINISH("90007", "当前工单状态不能处理完成操作"),
    ;

    /**
     * 状态码
     */
    private final String code;
    /**
     * 描述信息
     */
    private final String desc;

    WorkOrderResponseCode(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}
