package com.choice.cloud.architect.groot.dto;

import java.util.List;

import javax.validation.constraints.NotBlank;

import com.choice.cloud.architect.groot.remote.AppListResponse;
import lombok.Data;
import org.springframework.util.Assert;

/**
 * @ClassName CreateAppIdRequest
 * @Description 创建AppId请求
 * @Author LZ
 * @Date 2020/4/12 8:20
 * @Version 1.0
 */
@Data
public class CreateAppIdRequest {
    @NotBlank(message = "应用Id(AppId)不能为空")
    private String appId;
    @NotBlank(message = "应用名称不能为空")
    private String name;
    @NotBlank(message = "部门id不能为空")
    private String orgId;
    @NotBlank(message = "部门名称不能为空")
    private String orgName;
    @NotBlank(message = "应用负责人不能为空，apollo")
    private String ownerName;
    private List<String> admins;

    public static CreateAppIdRequest instance(AppListResponse appInfo) {
        Assert.notNull(appInfo, "没有获取到应用信息");
        CreateAppIdRequest request = new CreateAppIdRequest();
        request.setAppId(appInfo.getAppCode());
        request.setName(appInfo.getAppCode());
        request.setOrgId(appInfo.getDomainArchitectureId());
        request.setOrgName(appInfo.getDomainArchitectureName());
        request.setOwnerName("apollo");
        return request;
    }
}
