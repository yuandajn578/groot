package com.choice.cloud.architect.groot.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

/**
 * 迭代表(TGrIteration)实体类
 *
 * @author makejava
 * @since 2020-08-19 16:28:47
 */
@Data
public class TGrIteration implements Serializable {
    private static final long serialVersionUID = 648694755311363659L;
    /**
     * 自增主键
     */
    private Long id;
    /**
     * 业务id
     */
    private String oid;
    /**
     * 项目oid
     */
    private String projectId;
    /**
     * 项目名称
     */
    private String projectName;
    /**
     * 迭代名称
     */
    private String iterationName;
    /**
     * 迭代描述
     */
    private String iterationDesc;
    /**
     * 预计完成时间
     */
    private String exceptCompleteTime;
    /**
     * 实际完成时间
     */
    private String actualCompleteTime;
    /**
     * 迭代状态(未执行unexecuted  迭代中iteration  已完成completed  已提前关闭stopped)
     */
    private String iterationStatus;
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