package com.choice.cloud.architect.groot.process.service;

import com.alibaba.fastjson.JSONObject;
import com.choice.cloud.architect.groot.exception.ServiceException;
import com.choice.cloud.architect.groot.process.code.AccountResponseCode;
import com.choice.cloud.architect.groot.process.config.ProcessConfig;
import com.choice.cloud.architect.groot.process.dto.ProcessInstanceInputDTO;
import com.choice.cloud.architect.groot.process.enums.DingTalkProcessEventTypeEnum;
import com.choice.cloud.architect.groot.process.enums.DingTalkProcessResultEnum;
import com.choice.cloud.architect.groot.process.enums.DingTalkProcessStatusEnum;
import com.choice.cloud.architect.groot.response.ResponseData;
import com.choice.cloud.architect.groot.response.code.SysResponseCode;
import com.choice.cloud.architect.groot.service.inner.GrPublishAuditService;
import com.dingtalk.api.request.OapiProcessinstanceCreateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @ClassName ProcessService
 * @Description 审批流处理服务
 * @Author Guangshan Wang
 * @Date 2020/3/9/009 23:22
 */
@Slf4j
@Service
public class ProcessService {

    @Autowired
    private GrPublishAuditService grPublishAuditService;
    @Autowired
    private MessageCenterProcessFeignClient messageCenterProcessFeignClient;
    @Resource
    private ProcessConfig processConfig;

    /**
     * 处理接受到的回调消息
     * @param msg
     */
    public void handleProcessMqMsg(String msg) {
        log.info("ProcessService handleProcessMqMsg msg:{}", msg);
        JSONObject processJson = (JSONObject) JSONObject.parse(msg);
        // 应用发布审核的情况
        if (DingTalkProcessEventTypeEnum.BPMS_INSTANCE_CHANGE.getCode().equals(processJson.getString("EventType"))
                && processConfig.getProcessCode().equals(processJson.getString("processCode"))) {
            if (DingTalkProcessStatusEnum.FINISH.getCode().equals(processJson.getString("type"))
                    && DingTalkProcessResultEnum.AGREE.getCode().equals(processJson.getString("result"))) {
                // 审批通过，修改发布审核单的状态
                String processId = processJson.getString("processInstanceId");
                log.info("[接收钉钉审批回调消息]审批通过，对应的processInstanceId：{}", processId);
                grPublishAuditService.approvedProcess(processId);

            } else if (DingTalkProcessStatusEnum.FINISH.getCode().equals(processJson.getString("type"))
                    && DingTalkProcessResultEnum.REFUSE.getCode().equals(processJson.getString("result"))) {
                log.info("[接收钉钉审批回调消息]审批拒绝，对应的processInstanceId：{}", processJson.getString("processInstanceId"));
                // 拒绝
                grPublishAuditService.refuseProcess(processJson.getString("processInstanceId"));

            } else if (DingTalkProcessStatusEnum.TERMINATE.getCode().equals(processJson.getString("type"))) {
                log.info("[接收钉钉审批回调消息]审批撤回，对应的processInstanceId：{}", processJson.getString("processInstanceId"));
                // 撤回
                grPublishAuditService.withdrawProcess(processJson.getString("processInstanceId"));
            }
        }
    }

    public void handleProcessMqMsg4Compensate(String msg) {
        log.info("ProcessService handleProcessMqMsg msg:{}", msg);
        JSONObject processJson = (JSONObject) JSONObject.parse(msg);
        // 应用发布审核的情况
        if (DingTalkProcessEventTypeEnum.BPMS_INSTANCE_CHANGE.getCode().equals(processJson.getString("eventType"))
                && processConfig.getProcessCode().equals(processJson.getString("processCode"))) {
            if (DingTalkProcessStatusEnum.FINISH.getCode().equals(processJson.getString("type"))
                    && DingTalkProcessResultEnum.AGREE.getCode().equals(processJson.getString("result"))) {
                // 审批通过，修改发布审核单的状态
                String processId = processJson.getString("processInstanceId");
                grPublishAuditService.approvedProcess(processId);

            } else if (DingTalkProcessStatusEnum.FINISH.getCode().equals(processJson.getString("type"))
                    && DingTalkProcessResultEnum.REFUSE.getCode().equals(processJson.getString("result"))) {
                // 拒绝
                grPublishAuditService.refuseProcess(processJson.getString("processInstanceId"));
            } else if (DingTalkProcessStatusEnum.TERMINATE.getCode().equals(processJson.getString("type"))) {
                // 撤回
                grPublishAuditService.withdrawProcess(processJson.getString("processInstanceId"));
            }
        }
    }


    /**
     * 发起一个发布应用的审批流
     */
    public String startProcess4Publish(ProcessInstanceInputDTO processRequest) {
        if (processRequest == null || processRequest.getApprovers() == null
                || processRequest.getOriginatorUserId() == null) {
            throw new ServiceException(SysResponseCode.PARAM_ILLEGAL);
        }

        // 发起审批流
        ResponseData<String> response = messageCenterProcessFeignClient.startProcessInstance(processRequest);
        log.info("ProcessService startProcess4Publish 向 messagecenter 发起审批流请求request:{}, response:{}", processRequest, response);
        if (response != null && "10000".equals(response.getCode())) {
            String processInstanceId = response.getData();
            return processInstanceId;
        }
        throw new ServiceException(AccountResponseCode.START_DING_TALK_PROCESS_ERROR);
    }


    public String createPublishAuditProcess(OapiProcessinstanceCreateRequest request) {

        // 发起审批流
        ResponseData<String> response = messageCenterProcessFeignClient.createProcessInstance(request);

        return response.getData();
    }

}
