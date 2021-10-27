package com.choice.cloud.architect.groot.process.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

/**
 * @ClassName ProcessConfig
 * @Description 审批流的基本配置信息
 * @Author Guangshan Wang
 * @Date 2020/3/9/009 23:23
 */
@Getter
@Repository
public class ProcessConfig {

//    public static final String processCode = "PROC-C13F20D5-4B9B-4513-BBC0-BFEBEBFFF505";
//            = "PROC-535B3C10-BD70-4977-979F-85C4A8965B58"
    /**
     * 钉钉审批编码
     */
    @Value("${dingtalk.process.code}")
    private String processCode;

    @Value("${dingtalk.process.pro.approver}")
    private String proApprover;

    @Value("${dingtalk.process.pro.emergency.approver}")
    private String proEmergencyApprover;

    @Value("${dingtalk.process.pro.cc}")
    private String proCc;

    /**
     * 配置生产环境，日常、紧急、研发自测发布审批人
     * 目前配置的是风君，可以配置多个
     */
    @Value("${dingtalk.process.pro.audit.approver}")
    private String proAuditApprover;

}
