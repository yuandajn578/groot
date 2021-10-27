package com.choice.cloud.architect.groot.process.mq;

import java.nio.charset.StandardCharsets;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.choice.cloud.architect.groot.process.service.ProcessService;
import com.choice.driver.ali.mq.annotation.AliMQListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName ProcessMqConsumer
 * @Description 接受钉钉回调的MQ消息
 *  钉钉回调信息由messageCenter接受，并以MQ形式转发出去，由业务方订阅
 *  这里的groot就是业务方，接收审批回调，然后处理业务逻辑
 * @Author Guangshan Wang
 * @Date 2020/3/9/009 15:35
 */
@Slf4j
@Service
public class ProcessMqConsumer {

    @Autowired
    private ProcessService processService;

    /**
     * 接收钉钉审批消息
     * @param message
     * @param context
     * @return
     */
    @AliMQListener(groupId = "#{@aliMQProperties.consumer.newdingtalk.groupId}",topic = "#{@aliMQProperties.consumer.newdingtalk.topic}",tag = "#{@aliMQProperties.consumer.newdingtalk.tag}")
    public Action consumeDingTalkProcessMessage(Message message, ConsumeContext context) {
        byte[] messageBody = message.getBody();
        String body = new String(messageBody, StandardCharsets.UTF_8);
        log.info("[接收钉钉审批回调消息]收到消息：{}", body);
        boolean consumeSuccess = false;
        try {
            log.info("[接收钉钉审批回调消息]消费消息，对应的消息id：{}", message.getMsgID());
            processService.handleProcessMqMsg(body);
            consumeSuccess = true;
        } catch (Exception e) {
            log.info("ProcessMqConsumer consumeDingTalkProcessMessage Occur Exception: {}", ExceptionUtils.getStackTrace(e));
        }
        return consumeSuccess ? Action.CommitMessage : Action.ReconsumeLater;
    }

}
