package com.choice.cloud.architect.groot.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 查询应用变更请求入参
 * </p>
 *
 * @author zhangkun
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class ListChangeRequest extends PageRequest {

    @ApiModelProperty(value = "变更名")
    private String changeName;

    @ApiModelProperty(value = "应用ID(银河)")
    private String appId;

    @ApiModelProperty(value = "应用编码(银河)")
    private String appCode;

    /**
     * 中文应用名称(银河)
     */
    private String appName;

    /**
     * 仓库地址
     */
    private String gitlabAddress;

    /**
     * 分支名称
     */
    private String branchName;

    /**
     * apollo对应应用id
     */
    private String apolloApplicationId;

    /**
     * sprint地址
     */
    private String sprintAddress;

    /**
     * sprint提测单地址
     */
    private String sprintTestApplicationAddress;

    /**
     * 描述信息
     */
    private String description;

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

    /** 搜索条件 */
    private String searchValue;

    private List<String> ids;
}
