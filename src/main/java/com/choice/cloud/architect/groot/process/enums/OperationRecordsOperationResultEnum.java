package com.choice.cloud.architect.groot.process.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * <p>
 * 操作记录列表中操作结果枚举类
 * </p>
 *
 * @author LZ
 * @date Created in 2020/6/10 16:12
 */
@Getter
public enum OperationRecordsOperationResultEnum {
    /**
     * 操作结果，分为
     *
     * NONE（无），AGREE（同意），REFUSE（拒绝）
     */
    NONE("none", "无"),
    AGREE("agree", "同意"),
    REFUSE("refuse", "拒绝"),
    ;

    private String code;
    private String desc;

    OperationRecordsOperationResultEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String findDescByCode(String code) {
        if (StringUtils.isBlank(code)) {
            return "";
        }
        for (OperationRecordsOperationResultEnum resultEnum : OperationRecordsOperationResultEnum.values()) {
            if (code.equalsIgnoreCase(resultEnum.getCode())) {
                return resultEnum.desc;
            }
        }
        return "";
    }
}
