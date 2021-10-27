package com.choice.cloud.architect.groot.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 创建应用变更入参
 * </p>
 *
 * @author zhangkun
 */
@Data
@ApiModel(description = "应用变更单参数")
public class CreateChangeRequest {
    /**
     * 变更描述
     */
    @ApiModelProperty(value = "变更描述",required = true)
    @NotEmpty(message = "变更原因不能为空")
    private String changeName;

    /**
     * 应用编码(银河)
     */
    @ApiModelProperty(value = "应用ID(银河)",required = true)
    @NotEmpty(message = "应用ID(银河)不能为空")
    private String appId;

    @ApiModelProperty(value = "应用编码(银河)",required = true)
    @NotEmpty(message = "应用编码不能为空")
    private String appCode;
    /**
     * 中文应用名称(银河)
     */
    @ApiModelProperty(value = "中文应用名称(银河)",required = true)
    private String appName;

 /*   @ApiModelProperty(value = "开发者",required = true)
    @NotEmpty(message = "开发者不能为空")
    private String developer;

    @ApiModelProperty(value = "测试者",required = true)
    @NotEmpty(message = "测试者不能为空")
    private String conner;*/

    /**
     * 仓库地址
     */
    @ApiModelProperty(value = "仓库地址",required = true)
    @NotEmpty(message = "仓库地址不能为空")
    private String gitlabAddress;

    @ApiModelProperty(value = "是否创建新分支 create创建 select选择已有分支",required = true)
    @NotEmpty(message = "是否创建新分支")
    private String branchChange;

    /**
     * 分支名称
     */
    @ApiModelProperty(value = "分支名称",required = true)
    @NotEmpty(message = "分支名称不能为空")
    private String branchName;

    /**
     * 描述信息
     */
    @ApiModelProperty(value = "描述信息")
    private String description;

    /** 克隆分支 */
    @ApiModelProperty(value = "克隆分支",required = true)
    private String cloneBranch;

    /** 克归属项目 */
    @ApiModelProperty(value = "克归属项目",required = true)
    private String belongProject;

    /** 计划发布时间 */
    @ApiModelProperty(value = "计划发布时间",required = true)
    @NotNull(message = "计划发布时间不能为空")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;

    /** 发布后是否删除分支 */
    @ApiModelProperty(value = "发布后是否删除分支",required = true)
    @NotNull(message = "发布后是否删除分支不能为空")
    private Integer  deleteBranch;

    @ApiModelProperty(value = "成员信息", required = true)
    @NotEmpty(message = "成员信息不能为空")
    private List<CreateMemberRequest> members;

    @ApiModelProperty(value = "迭代关联", required = true)
    private String iterationId;
}
