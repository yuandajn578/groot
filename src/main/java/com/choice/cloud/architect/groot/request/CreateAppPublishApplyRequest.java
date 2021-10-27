package com.choice.cloud.architect.groot.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhangkun
 */
@Data
@ApiModel(description = "应用构建申请单参数")
public class CreateAppPublishApplyRequest {
    /**
     * 父级申请单编码
     */
    @ApiModelProperty(value = "父级申请单编码")
    private String parentFormId;

    /**
     * 根申请单编码
     */
    @ApiModelProperty(value = "根申请单编码")
    private String rootFormId;

    /**
     * 申请单类型（1：开发构建单，2：提测单，3：灰度提测单，4：正式环境发布审核单，5：正式环境发布单）
     */
    @ApiModelProperty(value = "申请单类型（1：开发构建单，2：提测单，3：灰度提测单，4：正式环境发布审核单，5：正式环境发布单）")
    private String formType;

    /**
     * 变更编码
     */
    @ApiModelProperty(value = "变更编码")
    private String changeId;

    /**
     * 环境编码
     */
    @ApiModelProperty(value = "环境编码")
    private String envId;

    /**
     * apollo发布环境
     */
    @ApiModelProperty(value = "apollo发布环境")
    private String apolloEnv;

    /**
     * apollo发布idc
     */
    @ApiModelProperty(value = "apollo发布idc")
    private String apolloIdc;

    /**
     * 描述信息
     */
    @ApiModelProperty(value = "描述信息    ")
    private String description;
}
