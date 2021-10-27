package com.choice.cloud.architect.groot.dto.project;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 * 编辑保存项目请求体
 * </p>
 *
 * @author LZ
 * @date Created in 2020/8/19 16:53
 */
@Data
public class SaveProjectRequestDTO {

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
     * 更新人
     */
    private String uUser;
}
