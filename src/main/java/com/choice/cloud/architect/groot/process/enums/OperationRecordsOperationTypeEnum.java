package com.choice.cloud.architect.groot.process.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * <p>
 * 操作记录列表中操作类型枚举类
 * </p>
 *
 * @author LZ
 * @date Created in 2020/6/10 16:11
 */
@Getter
public enum OperationRecordsOperationTypeEnum {
    /**
     * 操作类型，分为
     *
     * EXECUTE_TASK_NORMAL（正常执行任务），EXECUTE_TASK_AGENT（代理人执行任务），APPEND_TASK_BEFORE（前加签任务），
     * APPEND_TASK_AFTER（后加签任务），REDIRECT_TASK（转交任务），START_PROCESS_INSTANCE（发起流程实例），
     * TERMINATE_PROCESS_INSTANCE（终止(撤销)流程实例），FINISH_PROCESS_INSTANCE（结束流程实例），ADD_REMARK（添加评论）
     */
    EXECUTE_TASK_NORMAL("execute_task_normal", "正常执行任务"),
    EXECUTE_TASK_AGENT("execute_task_agent", "代理人执行任务"),
    APPEND_TASK_BEFORE("append_task_before", "前加签任务"),
    APPEND_TASK_AFTER("append_task_after", "后加签任务"),
    REDIRECT_TASK("redirect_task", "转交任务"),
    START_PROCESS_INSTANCE("start_process_instance", "发起流程实例"),
    TERMINATE_PROCESS_INSTANCE("terminate_process_instance", "终止(撤销)流程实例"),
    FINISH_PROCESS_INSTANCE("finish_process_instance", "结束流程实例"),
    ADD_REMARK("add_remark", "添加评论"),
    ;

    private String code;
    private String desc;

    OperationRecordsOperationTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String findDescByCode(String code) {
        if (StringUtils.isBlank(code)) {
            return "";
        }
        for (OperationRecordsOperationTypeEnum typeEnum : OperationRecordsOperationTypeEnum.values()) {
            if (code.equalsIgnoreCase(typeEnum.getCode())) {
                return typeEnum.desc;
            }
        }
        return "";
    }
}
