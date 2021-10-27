package com.choice.cloud.architect.groot.remote;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName AppRoleRelationDTO
 * @Description 应用与角色成员关系
 * @Author Guangshan Wang
 * @Date 2020/2/11/011 22:02
 */
@Data
@ApiModel(description = "阿波罗变更单分页查询参数")
public class AppRoleRelationDTO {

    @ApiModelProperty(hidden = true)
    private Integer id;

    @ApiModelProperty(value = "变更单编码")
    private String oid;

    @ApiModelProperty(value = "变更单编码")
    private String appCode;

    @ApiModelProperty(value = "变更单编码")
    private String appName;

    @ApiModelProperty(value = "变更单编码")
    private String roleCode;

    @ApiModelProperty(value = "变更单编码")
    private String roleName;

    @ApiModelProperty(value = "变更单编码")
    private String userId;

    @ApiModelProperty(value = "变更单编码")
    private String userName;

    @ApiModelProperty(value = "变更单编码")
    private String createUser;

    @ApiModelProperty(value = "变更单编码")
    private Date createTime;

    @ApiModelProperty(value = "变更单编码")
    private String updateUser;

    @ApiModelProperty(value = "变更单编码")
    private Date updateTime;

    @ApiModelProperty(value = "变更单编码")
    private Boolean deleteFlag;

}
