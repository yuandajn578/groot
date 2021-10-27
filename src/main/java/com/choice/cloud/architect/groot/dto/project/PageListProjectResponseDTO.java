package com.choice.cloud.architect.groot.dto.project;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 * 分页查询项目响应体
 * </p>
 *
 * @author LZ
 * @date Created in 2020/8/19 16:50
 */
@Data
public class PageListProjectResponseDTO {
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
     * 创建时间
     */
    private LocalDateTime ctime;
    /**
     * 更新时间
     */
    private LocalDateTime uTime;

}
