package com.choice.cloud.architect.groot.request;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/24 10:47
 */
@Data
@ApiModel(description = "回滚到上一个版本")
public class DeploymentRollbackRequest {

    @NotBlank(message = "appCode不能为空")
    private String appCode;

    @NotBlank(message = "envType不能为空")
    private String envType;

    @NotBlank(message = "envCode不能为空")
    private String envCode;

    private String publishOid;
}
