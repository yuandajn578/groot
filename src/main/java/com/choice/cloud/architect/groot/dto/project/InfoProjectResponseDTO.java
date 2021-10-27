package com.choice.cloud.architect.groot.dto.project;

import lombok.Data;

/**
 * <p>
 * 项目详情响应体
 * </p>
 *
 * @author LZ
 * @date Created in 2020/8/19 16:51
 */
@Data
public class InfoProjectResponseDTO {
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
}
