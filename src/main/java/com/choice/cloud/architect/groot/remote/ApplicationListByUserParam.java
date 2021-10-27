package com.choice.cloud.architect.groot.remote;

import com.choice.cloud.architect.groot.remote.enums.RoleEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Set;

/**
 * @ClassName ApplicationListByUserParam
 * @Description 根据用户与角色查询应用列表
 * @Author Guangshan Wang
 * @Date 2020/3/10/010 15:19
 */
@Data
@ToString
@ApiModel(description = "应用参数")
public class ApplicationListByUserParam {

    /**
     * 用户查询用户权限下的应用列表使用
     */
    @ApiModelProperty(value = "用户ID")
    private String userId;
    /**
     * 用户角色列表，配合用户权限一起使用
     */
    @ApiModelProperty(value = "用户角色 AppOps,RD,publish,QA,DBA,SE,SRE,PE")
    private List<RoleEnum> roleEnumList;

}
