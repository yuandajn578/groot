package com.choice.cloud.architect.groot.model.workordersystem;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

/**
 * 工单表(TGrWorkOrder)实体类
 *
 * @author makejava
 * @since 2020-08-10 11:02:10
 */
@Data
public class TGrWorkOrder implements Serializable {
    private static final long serialVersionUID = 841897179530766430L;
    /**
     * 自增主键
     */
    private Long id;
    /**
     * 工单编码（UUID）
     */
    private String orderCode;
    /**
     * 工单名称
     */
    private String orderName;
    /**
     * 工单类型编码
     */
    private String orderTypeCode;
    /**
     * 工单类型名字
     */
    private String orderTypeName;
    /**
     * 工单状态(待接单，处理完成，已驳回，主动关闭)
     */
    private String orderStatus;
    /**
     * 工单归属环境(多个环境使用逗号隔开)
     */
    private String orderBelongEnv;
    /**
     * 工单申请人id
     */
    private String orderApplyUserId;
    /**
     * 工单申请人
     */
    private String orderApplyUser;
    /**
     * 工单申请原因
     */
    private String orderApplyReason;
    /**
     * 工单处理人id
     */
    private String orderHandlerUserId;
    /**
     * 工单处理人
     */
    private String orderHandlerUser;
    /**
     * 工单处理反馈
     */
    private String orderHandlerFeedback;
    /**
     * 创建人
     */
    private String cUser;
    /**
     * 创建时间
     */
    private LocalDateTime cTime;
    /**
     * 更新人
     */
    private String uUser;
    /**
     * 更新时间
     */
    private LocalDateTime uTime;
    /**
     * 删除状态 0 未删除  1已删除
     */
    private Integer isDeleted;
}