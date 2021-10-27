package com.choice.cloud.architect.groot.remote;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author: zhangguoquan
 * @Date: 2020/5/12 15:12
 */
@Data
public class DomainArchitectureDTO {

    @ApiModelProperty(hidden = true)
    private Integer id;

    @ApiModelProperty(value = "架构域id")
    private String oid;

    @ApiModelProperty(value = "架构域名称")
    private String architectureName;

    @ApiModelProperty(value = "CONFIRMING审核中 CONFIRMED已审核 REJECTED已驳回")
    private String status;

    @ApiModelProperty(value = "部门")
    private String department;

    @ApiModelProperty(value = "主架构负责人")
    private String ownerId;

    @ApiModelProperty(value = "主架构负责人名称")
    private String ownerName;

    @ApiModelProperty(value = "备架构负责人")
    private String ownerIdBackUp;

    @ApiModelProperty(value = "备架构负责人名称")
    private String ownerNameBackUp;

    @ApiModelProperty(value = "质量负责人")
    private String qaId;

    @ApiModelProperty(value = "质量负责人名称")
    private String qaName;

    @ApiModelProperty(value = "备架构负责人名称")
    private String qaIdBackUp;

    @ApiModelProperty(value = "备架构负责人名称")
    private String qaNameBackUp;

    @ApiModelProperty(value = "DBA负责人")
    private String dbaId;

    @ApiModelProperty(value = "DBA负责人名称")
    private String dbaName;

    @ApiModelProperty(value = "备DBA负责人名称")
    private String dbaIdBackUp;

    @ApiModelProperty(value = "备DBA负责人名称")
    private String dbaNameBackUp;

    @ApiModelProperty(value = "SRE负责人")
    private String sreId;

    @ApiModelProperty(value = "SRE负责人名称")
    private String sreName;

    @ApiModelProperty(value = "备SRE负责人")
    private String sreIdBackUp;

    @ApiModelProperty(value = "备SRE负责人名称")
    private String sreNameBackUp;

    @ApiModelProperty(value = "前端负责人")
    private String feId;

    @ApiModelProperty(value = "前端负责人名称")
    private String feName;

    @ApiModelProperty(value = "备前端负责人")
    private String feIdBackUp;

    @ApiModelProperty(value = "备前端负责人名称")
    private String feNameBackUp;

    @ApiModelProperty(value = "安全负责人")
    private String seId;

    @ApiModelProperty(value = "安全负责人名称")
    private String seName;

    @ApiModelProperty(value = "安全负责人")
    private String seIdBackUp;

    @ApiModelProperty(value = "备安全负责人名称")
    private String seNameBackUp;

    @ApiModelProperty(value = "架构与描述")
    private String architectureDesc;

    @ApiModelProperty(value = "创建人")
    private String createUser;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改人")
    private String updateUser;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    @ApiModelProperty(value = "删除标志")
    private Integer deleteFlag;

}

