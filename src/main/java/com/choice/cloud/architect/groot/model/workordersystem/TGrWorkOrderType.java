package com.choice.cloud.architect.groot.model.workordersystem;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

/**
 * 工单类型表(TGrWorkOrderType)实体类
 *
 * @author makejava
 * @since 2020-08-10 11:02:17
 */
@Data
public class TGrWorkOrderType implements Serializable {
    private static final long serialVersionUID = 666791630901128386L;
    /**
     * 自增主键
     */
    private Long id;
    /**
     * 工单类型编码
     */
    private String orderTypeCode;
    /**
     * 工单类型名称
     */
    private String orderTypeName;
    /**
     * 工单类型描述
     */
    private String orderTypeDesc;
    /**
     * 工单类型归属用户id
     */
    private String orderTypeBelongUserId;
    /**
     * 工单类型归属用户
     */
    private String orderTypeBelongUser;
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