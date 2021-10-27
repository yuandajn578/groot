package com.choice.cloud.architect.groot.mq.publish;

import lombok.Data;

/**
 * <p>
 * 发布成功未验收，发工作通知功能的消息体
 * </p>
 *
 * @author LZ
 * @date Created in 2020/9/9 13:13
 */
@Data
public class PublishSendNoticeDTO {
    /**
     * 变更单id
     */
    private String changeId;
    /**
     * 通知阶段
     */
    private String phaseCode;
    /**
     * 发布人员
     */
    private String publishUserId;
    /**
     * 应用owner
     */
    private String ownerId;
    /**
     * 架构域owner
     */
    private String domainOwnerId;
}
