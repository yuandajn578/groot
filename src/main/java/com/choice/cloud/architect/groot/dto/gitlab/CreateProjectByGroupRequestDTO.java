package com.choice.cloud.architect.groot.dto.gitlab;

import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * <p>
 * 在gitlab分组group下创建项目project请求体
 * </p>
 *
 * @author LZ
 * @date Created in 2020/7/24 15:28
 */
@Data
public class CreateProjectByGroupRequestDTO {
    @NotBlank(message = "Gitlab分组名称不能为空")
    private String groupName;
    @NotBlank(message = "Gitlab项目名称不能为空")
    private String projectName;
}
