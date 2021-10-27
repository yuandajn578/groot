package com.choice.cloud.architect.groot.dto.iteration;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * <p>
 * 新增迭代请求体
 * </p>
 *
 * @author LZ
 * @date Created in 2020/8/19 16:49
 */
@Data
public class CreateIterActionRequestDTO {
    /**
     * 项目oid
     */
    //@NotBlank(message = "项目oid不能为空")
    private String projectId;
    /**
     * 项目名称
     */
    //@NotBlank(message = "项目名称不能为空")
    private String projectName;
    /**
     * 迭代名称
     */
    @NotBlank(message = "迭代名称不能为空")
    private String iterationName;
    /**
     * 迭代描述
     */
    @NotBlank(message = "迭代描述不能为空")
    private String iterationDesc;
    /**
     * 预计完成时间
     */
    @NotBlank(message = "预计完成时间不能为空")
    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private String exceptCompleteTime;
    /**
     * 实际完成时间
     */
    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private String actualCompleteTime;
}
