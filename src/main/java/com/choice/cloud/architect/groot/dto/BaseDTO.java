package com.choice.cloud.architect.groot.dto;

import java.util.Date;

import lombok.Data;

/**
 * <p>
 * 业务基础DTO
 * </p>
 *
 * @author LZ
 * @date Created in 2020/8/10 11:18
 */
@Data
public class BaseDTO {
    /**
     * 创建人
     */
    private String cUser;
    /**
     * 创建时间
     */
    private Date cTime;
    /**
     * 更新人
     */
    private String uUser;
    /**
     * 更新时间
     */
    private Date uTime;
    /**
     * 删除状态 0 未删除  1已删除
     */
    private Integer isDeleted;
}
