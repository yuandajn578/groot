package com.choice.cloud.architect.groot.request;

import com.choice.cloud.architect.groot.dto.AppEnvPublishConfigDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 创建应用环境入参
 * </p>
 *
 * @author zhangkun
 */
@Data
@ToString
@ApiModel(description = "创建应用环境")
public class CreateEnvRequest {
    /**
     * 环境类型（0：开发，1：测试，2：正式）
     */
    @ApiModelProperty(value = "环境类型（dev/test/online）")
    @NotNull(message = "环境类型不能为空")
    private String envType;

    /**
     * 环境名称（英文）
     */
    @ApiModelProperty(value = "环境编码")
    @NotNull(message = "服务分组不能为空")
    private String envCode;

    /**
     * 环境中文描述
     */
    @ApiModelProperty(value = "环境名称")
    private String envName;

    /**
     * 描述信息
     */
    @ApiModelProperty(value = "备注")
    private String description;

    /**
     * 到期时间
     */
    @ApiModelProperty(value = "到期时间")
    @Future(message = "到期时间必须是一个未来日期")
    private Date expireDate;

    /**
     * 环境编码
     */
    @ApiModelProperty(value = "应用编码")
    private String appCode;

    /**
     * 计算资源规格
     */
    @ApiModelProperty(value = "计算资源规格")
    private String resourceSpecId;

    /**
     * 变更单id
     */
    @ApiModelProperty(value = "变更单id")
    private String changeId;

    /**
     * 发布配置
     */
    @ApiModelProperty(value = "发布配置")
    private AppEnvPublishConfigDTO publishConfig;
}
