package com.choice.cloud.architect.groot.process.service;

import com.choice.cloud.architect.groot.process.dto.*;
import com.choice.cloud.architect.groot.process.param.WebhookTextParam;
import com.choice.cloud.architect.groot.response.ResponseData;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
import com.dingtalk.api.request.OapiProcessinstanceCreateRequest;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiProcessinstanceGetResponse;
import com.dingtalk.api.response.OapiRobotSendResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @ClassName MessageCenterProcessFeignClient
 * @Description 钉钉审批流
 * @Author Guangshan Wang
 * @Date 2020/3/9/009 22:20
 */
@FeignClient(value = "MESSAGE-CENTER")
public interface MessageCenterProcessFeignClient {

    /**
     * 发起审批
     * @param reqBody
     * @return
     */
    @PostMapping("/api/dingtalk/process/start")
    ResponseData<String> startProcessInstance(@RequestBody ProcessInstanceInputDTO reqBody);

    /**
     * 发送机器人信息
     * @param webhookTextParam
     * @return
     */
    @PostMapping("/api/dingtalk/sendWebhookMessage")
    ResponseData<String> sendWebhookMessage(@RequestParam("token") String token, @RequestBody WebhookTextParam webhookTextParam);

    /**
     * 发送机器人消息（新接口）
     * @param request
     * @return
     */
    @PostMapping("/api/dingtalk/sendWebhookMessageNew")
    ResponseData<String> sendWebhookMessageNew(@RequestParam("accessToken") String accessToken, @RequestBody OapiRobotSendRequest request);

    /**
     * 发送工作通知信息
     * @param sendCorporationNoticeParam
     * @return
     */
    @PostMapping("/api/dingtalk/sendCorporationNotice")
    ResponseData<String> sendCorporationNotice(@RequestBody OapiMessageCorpconversationAsyncsendV2Request sendCorporationNoticeParam);

    @PostMapping("/api/dingtalk/process/create")
    ResponseData<String> createProcessInstance(@RequestBody OapiProcessinstanceCreateRequest req);

    /**
     * 根据审批实例id获取审批详情
     * @param query
     * @return
     */
    @PostMapping("/api/dingtalk/process/get")
    ResponseData<OapiProcessinstanceGetResponse.ProcessInstanceTopVo> getProcessById(@RequestBody ProcessInfoQueryDTO query);
}
