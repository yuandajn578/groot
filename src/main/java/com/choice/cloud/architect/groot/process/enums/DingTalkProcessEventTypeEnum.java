package com.choice.cloud.architect.groot.process.enums;

import lombok.Getter;

/**
 * @ClassName DingTalkProcessStatusEnum
 * @Description 钉钉审批流的事件类型枚举
 * @Author Guangshan Wang
 * @Date 2020/3/10/010 8:35
 */
@Getter
public enum DingTalkProcessEventTypeEnum {

    BPMS_INSTANCE_CHANGE("bpms_instance_change", "审批实例开始，结束"),
    BPMS_TASK_CHANGE("bpms_task_change", "审批任务开始，结束，转交"),

    ;
    private String code;
    private String desc;

    DingTalkProcessEventTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
