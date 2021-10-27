package com.choice.cloud.architect.groot.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author zhangkun
 */
@Data
@ToString
@ApiModel(value = "创建发布审核单Model")
public class CreatePublishAuditRequest {
    /**
     * 发布名称
     */
    @NotNull
    @ApiModelProperty(value = "发布名称")
    @Size(min=4, max=255, message = "发布名称长度必须在4~255之间")
    private String publishName;

    /**
     * 环境类型 线上 线下
     */
    @ApiModelProperty(value = "环境类型 DEV/TEST/PRE/PROD")
    @NotNull
    private String envType;

    /**
     * 环境编码
     */
    @ApiModelProperty(value = "环境编码")
    @NotNull
    private String envCode;

    /**
     * 环境名称
     */
    @ApiModelProperty(value = "环境名称")
    @NotNull
    private String envName;

    /**
     * 发布类型 daily日常 emergency紧急发布
     */
    @NotNull
    @ApiModelProperty(value = "发布类型（daily日常 emergency紧急发布）")
    private String publishType;

    /**
     * 描述信息
     */
    @ApiModelProperty(value = "描述")
    private String publishDesc;

    /**
     * 变更单id集合
     */
    @ApiModelProperty(value = "变更单id集合")
    private List<String> changeIds;

    /**
     * 预计发布时间
     */
    @ApiModelProperty(value = "预计发布事件")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expectPublishTime;

    private String publishApplyUrl;

    private Integer dbChangeFlag;
}
