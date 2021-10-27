package com.choice.cloud.architect.groot.enums;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;

/**
 * <p>
 * 线上发布成功未验收，发工作通知阶段枚举类
 * </p>
 *
 * @author LZ
 * @date Created in 2020/9/9 10:56
 */
@Slf4j
@Getter
public enum PublishSendNoticePhaseEnum {
    ONE_PHASE("one_phase", "一阶段"),
    TWO_PHASE("two_phase", "二阶段"),
    THREE_PHASE("three_phase", "三阶段"),
    ;

    PublishSendNoticePhaseEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    private String code;
    private String name;

    /**
     * 根据阶段code计算对应的延迟时间
     * 用于验收完成请点击验收通过, 发布完成未点击验收通过，3h通知应用owner+应用发布人，6h通知架构域，10h冲哥
     * @param code
     * @return
     */
    public static Long getDeliverTimePhase(String code, String onePhaseTime, String twoPhaseTime, String threePhaseTime) {
        if (StringUtils.isBlank(code)) {
            log.info("阶段code为空");
            return 0L;
        }
        // 计算时间
        if (code.equalsIgnoreCase(PublishSendNoticePhaseEnum.ONE_PHASE.getCode())) {
            return Long.valueOf(StringUtils.trimToEmpty(onePhaseTime));
        } else if (code.equalsIgnoreCase(PublishSendNoticePhaseEnum.TWO_PHASE.getCode())) {
            return Long.valueOf(StringUtils.trimToEmpty(twoPhaseTime)) - Long.valueOf(StringUtils.trimToEmpty(onePhaseTime));
        } else if (code.equalsIgnoreCase(PublishSendNoticePhaseEnum.THREE_PHASE.getCode())) {
            return Long.valueOf(StringUtils.trimToEmpty(threePhaseTime)) - Long.valueOf(StringUtils.trimToEmpty(twoPhaseTime));
        } else {
            log.info("阶段code传值不正确");
            return 0L;
        }
    }

    /**
     * 根据阶段code计算下一阶段
     * @param code
     * @return
     */
    public static String getNextPhase(String code) {
        if (StringUtils.isBlank(code)) {
            log.info("阶段code为空");
            return "";
        }
        if (code.equalsIgnoreCase(PublishSendNoticePhaseEnum.ONE_PHASE.getCode())) {
            return PublishSendNoticePhaseEnum.TWO_PHASE.getCode();
        } else if (code.equalsIgnoreCase(PublishSendNoticePhaseEnum.TWO_PHASE.getCode())) {
            return PublishSendNoticePhaseEnum.THREE_PHASE.getCode();
        } else if (code.equalsIgnoreCase(PublishSendNoticePhaseEnum.THREE_PHASE.getCode())) {
            // 这里只发一次工作通知。
            return "";
        } else {
            log.info("阶段code传值不正确");
            return "";
        }
    }
}
