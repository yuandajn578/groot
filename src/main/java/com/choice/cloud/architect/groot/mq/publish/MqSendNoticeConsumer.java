package com.choice.cloud.architect.groot.mq.publish;

import java.nio.charset.StandardCharsets;

import com.alibaba.fastjson.JSON;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.choice.cloud.architect.groot.service.inner.GrChangeService;
import com.choice.driver.ali.mq.annotation.AliMQListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 发布成功未验收，发工作通知功能的延迟消息消费者
 * </p>
 *
 * @author LZ
 * @date Created in 2020/8/31 21:11
 */
@Slf4j
@Service
public class MqSendNoticeConsumer {

    @Autowired
    private GrChangeService grChangeService;

    @AliMQListener(
        groupId = "#{@aliMQProperties.consumer.sendnotice.groupId}",
        topic = "#{@aliMQProperties.consumer.sendnotice.topic}",
        tag = "#{@aliMQProperties.consumer.sendnotice.tag}"
    )
    public Action consumeSendNoticeMessage(Message message, ConsumeContext context) {
        byte[] messageBody = message.getBody();
        String body = new String(messageBody, StandardCharsets.UTF_8);
        log.info("收到消息：{}", body);
        PublishSendNoticeDTO sendNoticeDTO = JSON.parseObject(body, PublishSendNoticeDTO.class);
        boolean consumeSuccess = false;
        try {
            boolean handleFlag = grChangeService.handleChangeSendWebhookMessage(sendNoticeDTO);
            if (handleFlag) {
                log.info("[针对变更单发送钉钉工作通知]调用接口处理成功");
            } else {
                log.error("[针对变更单发送钉钉工作通知]调用接口处理失败");
            }
            consumeSuccess = true;
        } catch (Exception e) {
            log.error("消费失败：{}", ExceptionUtils.getStackTrace(e));
        }

        log.info("消息消费的状态：{}", consumeSuccess);
        return consumeSuccess ? Action.CommitMessage : Action.ReconsumeLater;
    }
}
