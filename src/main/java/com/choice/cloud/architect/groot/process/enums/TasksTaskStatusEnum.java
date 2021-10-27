package com.choice.cloud.architect.groot.process.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * <p>
 * 已审批任务列表（可以通过此列表获取已审批人）任务状态枚举类
 * </p>
 *
 * @author LZ
 * @date Created in 2020/6/10 16:12
 */
@Getter
public enum TasksTaskStatusEnum {
    /**
     * 任务状态，分为
     *
     * NEW（未启动），RUNNING（处理中），CANCELED（取消），COMPLETED（完成）
     */
    NEW("new", "未启动"),
    RUNNING("running", "处理中"),
    CANCELED("canceled", "取消"),
    COMPLETED("completed", "完成"),
    ;

    private String code;
    private String desc;

    TasksTaskStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String findDescByCode(String code) {
        if (StringUtils.isBlank(code)) {
            return "";
        }
        for (TasksTaskStatusEnum statusEnum : TasksTaskStatusEnum.values()) {
            if (code.equalsIgnoreCase(statusEnum.getCode())) {
                return statusEnum.desc;
            }
        }
        return "";
    }
}
