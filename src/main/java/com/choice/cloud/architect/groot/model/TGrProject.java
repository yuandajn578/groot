package com.choice.cloud.architect.groot.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

/**
 * 项目表(TGrProject)实体类
 *
 * @author makejava
 * @since 2020-08-19 16:28:53
 */
@Data
public class TGrProject implements Serializable {
    private static final long serialVersionUID = 693148494202559401L;
    /**
     * 自增主键
     */
    private Long id;
    /**
     * 业务id
     */
    private String oid;
    /**
     * 项目名称
     */
    private String projectName;
    /**
     * 项目描述
     */
    private String projectDesc;
    /**
     * 项目主负责人id
     */
    private String ownerId;
    /**
     * 项目主负责人姓名
     */
    private String ownerName;
    /**
     * 项目备负责人id
     */
    private String backUpOwnerId;
    /**
     * 项目备负责人姓名
     */
    private String backUpOwnerName;
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