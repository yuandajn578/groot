package com.choice.cloud.architect.groot.mq;

import com.alibaba.fastjson.JSON;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.bean.ProducerBean;
import com.choice.cloud.architect.groot.dto.CreateAppIdRequest;
import com.choice.cloud.architect.groot.gitlab.GitlabService;
import com.choice.cloud.architect.groot.remote.AppListResponse;
import com.choice.cloud.architect.groot.service.inner.CreateApolloService;
import com.choice.cloud.architect.groot.service.inner.GrAppEnvRelService;
import com.choice.driver.ali.mq.annotation.AliMQListener;
import com.choice.driver.ali.mq.properties.AliMQProperties;
import com.choice.driver.ali.mq.support.ConsumerItem;
import com.netflix.discovery.converters.Auto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * <p>
 * 银河系统创建应用后，初始化默认环境分组信息
 * </p>
 *
 * @author zhangkun
 */
@Slf4j
@Service
public class MilkWayCreateAppMqConsumer {
    private ProducerBean producerBean;
    private ConsumerItem grayTestConsumerItem;

    @Autowired
    private GrAppEnvRelService grAppEnvRelService;
    @Autowired
    private CreateApolloService createApolloService;
    @Autowired
    private GitlabService gitlabService;

    public MilkWayCreateAppMqConsumer(ProducerBean producerBean, AliMQProperties aliMQProperties) {
        this.producerBean = producerBean;
        this.grayTestConsumerItem = aliMQProperties.getConsumer().get("createapp");
    }

    @AliMQListener(groupId = "#{@aliMQProperties.consumer.createapp.groupId}",topic = "#{@aliMQProperties.consumer.createapp.topic}",tag = "#{@aliMQProperties.consumer.createapp.tag}")
    public Action consumeAppCreatedMessage(Message message, ConsumeContext context) {
        byte[] messageBody = message.getBody();
        String body = new String(messageBody, StandardCharsets.UTF_8);
        log.info("收到消息：{}", body);
        AppListResponse appInfo = JSON.parseObject(body, AppListResponse.class);
        boolean consumeSuccess = false;

        try {
            grAppEnvRelService.initDefaultValue(appInfo.getAppCode());

            // 消费消息来创建AppId和Idc
            CreateAppIdRequest instance = CreateAppIdRequest.instance(appInfo);
            createApolloService.createAppIdAndIdc(instance);

            // 创建gitlab信息
            boolean createGitLabFlag = gitlabService.createGitLab(appInfo.getAppCode());
            if (createGitLabFlag) {
                log.info("消费创建应用的消息来创建GitLab地址成功");
            } else {
                log.error("消费创建应用的消息来创建GitLab地址失败");
            }

            consumeSuccess = true;
        } catch (Exception e) {
            log.error("ConsumeAppCreatedMessage Occur Exception: {}", ExceptionUtils.getStackTrace(e));
        }

        log.info("消息消费的状态：{}", consumeSuccess);
        return consumeSuccess ? Action.CommitMessage : Action.ReconsumeLater;
    }
}
