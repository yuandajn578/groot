package com.choice.cloud.architect.groot.process.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * <p>
 * 已审批任务列表（可以通过此列表获取已审批人）任务结果枚举类
 * </p>
 *
 * @author LZ
 * @date Created in 2020/6/10 16:13
 */
@Getter
public enum TasksTaskResultEnum {
    /**
     * 结果，分为
     *
     * NONE（无），AGREE（同意），REFUSE（拒绝），REDIRECTED（转交）
     */
    NONE("none", "无"),
    AGREE("agree", "同意"),
    REFUSE("refuse", "拒绝"),
    REDIRECTED("redirected", "转交"),
    ;

    private String code;
    private String desc;

    TasksTaskResultEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String findDescByCode(String code) {
        if (StringUtils.isBlank(code)) {
            return "";
        }
        for (TasksTaskResultEnum resultEnum : TasksTaskResultEnum.values()) {
            if (code.equalsIgnoreCase(resultEnum.getCode())) {
                return resultEnum.desc;
            }
        }
        return "";
    }
}
