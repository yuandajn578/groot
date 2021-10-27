package com.choice.cloud.architect.groot.controller;

import com.alibaba.fastjson.JSON;

import com.aliyun.openservices.ons.api.SendResult;
import com.choice.cloud.architect.groot.dao.GrPublishMapper;
import com.choice.cloud.architect.groot.enums.PublishSendNoticePhaseEnum;
import com.choice.cloud.architect.groot.model.GrPublish;
import com.choice.cloud.architect.groot.mq.publish.MqSendNoticeService;
import com.choice.cloud.architect.groot.mq.publish.PublishSendNoticeDTO;
import com.choice.cloud.architect.groot.response.ResponseData;
import com.choice.cloud.architect.groot.service.inner.impl.GrBuildRecordServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 测试发布成功没有点击验收通过测试类
 * </p>
 *
 * @author LZ
 * @date Created in 2020/9/9 14:01
 */
@Slf4j
@RestController
@RequestMapping("/publish/notice/test")
public class TestController {

    @Autowired
    private MqSendNoticeService mqSendNoticeService;

    @Autowired
    private GrPublishMapper grPublishMapper;

    @Autowired
    private GrBuildRecordServiceImpl grBuildRecordService;

    @PostMapping("/sendMq")
    public ResponseData sendMq(@RequestParam(value = "changeId", required = true) String changeId) {
        // 发送延迟消息，发布成功未验收，发工作通知功能
        // 验收完成请点击验收通过, 发布完成未点击验收通过，3h通知应用owner+应用发布人，6h通知架构域，10h冲哥
        GrPublish grPublish = grPublishMapper.selectById("5f477a410c0d3e0001719c6c");
        //PublishSendNoticeDTO message = new PublishSendNoticeDTO();
        //message.setChangeId(changeId);
        //message.setPhaseCode(PublishSendNoticePhaseEnum.ONE_PHASE.getCode());
        PublishSendNoticeDTO message = grBuildRecordService.assembleMessage(grPublish);
        log.info("[发布回调]发送参数，message：{}", JSON.toJSONString(message));
        SendResult sendResult = mqSendNoticeService.sendNoticeMsg(JSON.toJSONString(message), PublishSendNoticePhaseEnum.ONE_PHASE.getCode());
        log.info("[发布回调]发送结果，sendResult：{}", JSON.toJSONString(sendResult));
        return ResponseData.createBySuccess(sendResult);
    }
}
