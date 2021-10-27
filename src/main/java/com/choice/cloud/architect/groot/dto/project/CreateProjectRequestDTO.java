package com.choice.cloud.architect.groot.dto.project;

import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * <p>
 * 新增项目请求体
 * </p>
 *
 * @author LZ
 * @date Created in 2020/8/19 16:49
 */
@Data
public class CreateProjectRequestDTO {
    /**
     * 项目名称
     */
    @NotBlank(message = "项目名称不能为空")
    private String projectName;
    /**
     * 项目描述
     */
    private String projectDesc;
    /**
     * 项目主负责人id
     */
    @NotBlank(message = "项目主负责人id不能为空")
    private String ownerId;
    /**
     * 项目主负责人姓名
     */
    @NotBlank(message = "项目主负责人名字不能为空")
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
