package com.choice.cloud.architect.groot.mq.publish;

import java.nio.charset.StandardCharsets;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.bean.ProducerBean;
import com.choice.cloud.architect.groot.enums.PublishSendNoticePhaseEnum;
import com.choice.driver.ali.mq.properties.AliMQProperties;
import com.choice.driver.ali.mq.support.ConsumerItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 发布成功未验收，发工作通知功能的延迟消息发送者
 * </p>
 *
 * @author LZ
 * @date Created in 2020/8/31 20:56
 */
@Slf4j
@Service
public class MqSendNoticeService {

    @Value("${publish.sendnotice.onephase.time}")
    private String onePhaseTime;

    @Value("${publish.sendnotice.twophase.time}")
    private String twoPhaseTime;

    @Value("${publish.sendnotice.threephase.time}")
    private String threePhaseTime;

    private ProducerBean producerBean;
    private ConsumerItem consumerItem;

    public MqSendNoticeService(ProducerBean producerBean, AliMQProperties aliMQProperties) {
        this.producerBean = producerBean;
        this.consumerItem = aliMQProperties.getConsumer().get("sendnotice");
    }

    /**
     * 发布成功未验收，发工作通知功能的延迟消息
     * @param message
     * @return
     */
    public SendResult sendNoticeMsg(String message, String phaseCode) {
        Message sendMessage = new Message();
        sendMessage.setTopic(consumerItem.getTopic());
        sendMessage.setTag(consumerItem.getTag());
        sendMessage.setBody(message.getBytes(StandardCharsets.UTF_8));
        // 查找对应的延迟时间
        Long deliverTimePhase = PublishSendNoticePhaseEnum.getDeliverTimePhase(phaseCode, onePhaseTime, twoPhaseTime, threePhaseTime);
        sendMessage.setStartDeliverTime(System.currentTimeMillis() + deliverTimePhase);
        SendResult result = producerBean.send(sendMessage);
        log.info("发布成功未验收，发工作通知功能的延迟消息，发送MQ消息:{},结果:{}", sendMessage, result);
        return result;
    }
}
