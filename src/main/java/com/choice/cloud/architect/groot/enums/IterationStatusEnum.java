package com.choice.cloud.architect.groot.enums;

import lombok.Getter;

/**
 * <p>
 * 迭代状态枚举类
 * </p>
 *
 * @author LZ
 * @date Created in 2020/8/19 17:27
 */
@Getter
public enum IterationStatusEnum {
    //未执行unexecuted  迭代中iteration  已完成completed  已提前关闭stopped
    UN_EXECUTED("unexecuted", "未执行"),
    ITERATION("iteration", "迭代中"),
    COMPLETED("completed", "已完成"),
    STOPPED("stopped", "提前关闭"),
    ;

    IterationStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private final String code;
    private final String desc;
}
