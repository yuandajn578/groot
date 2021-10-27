package com.choice.cloud.architect.groot.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author zhangkun
 */
@Data
public class BaseBizModel implements Serializable {
    private Integer id;

    /**
     * 主键id
     */
    private String oid;

    /**
     * 状态（0：关闭，1：开启）
     */
    private Integer status;

    /**
     * 删除标识(1：未删除 0：已删除)
     */
    private Integer deleteFlag;

    /**
     * 创建用户id
     */
    private String createUser;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新用户id
     */
    private String updateUser;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
