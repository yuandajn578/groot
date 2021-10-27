package com.choice.cloud.architect.groot.remote;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName ApplicationListParam
 * @Description 查询应用列表参数
 * @Author Guangshan Wang
 * @Date 2020/2/12/012 2:00
 */
@Data
@ApiModel(description = "应用参数")
public class AppListRequest {
    /**
     * 应用编码
     */
    @ApiModelProperty(value = "应用编码")
    private String appCode;
    /**
     * 架构域id
     */
    @ApiModelProperty(value = "架构域id")
    private String domainArchitectureId;
    /**
     * 应用状态
     */
    @ApiModelProperty(value = "应用状态")
    private String status;
    /**
     * 应用等级
     * @see com.choice.cloud.architect.milkyway.enums.AppRankEnum
     */
    @ApiModelProperty(value = "应用等级 普通应用：COMMON_APP  核心应用：CORE_APP")
    private String appRank;
    /**
     *
     * 应用类型
     * @see com.choice.cloud.architect.milkyway.enums.AppTypeEnum
     */
    @ApiModelProperty(value = "应用类型 前端：FRONT_END  后端：WEB_BACKEND 小程序：APPLET 安卓项目：ANDROID  IOS：IOS")
    private String appType;
    /**
     * 用户查询用户权限下的应用列表使用
     */
    @ApiModelProperty(value = "用户ID")
    private String userId;

}
