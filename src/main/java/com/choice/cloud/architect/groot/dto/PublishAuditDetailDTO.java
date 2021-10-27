package com.choice.cloud.architect.groot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zhangkun
 */
@Data
@ToString
@ApiModel("发布审核单详情Model")
public class PublishAuditDetailDTO extends BaseBizDTO{
    /**
     * 发布名称
     */
    @ApiModelProperty(value = "发布名称")
    private String publishName;

    /**
     * 关联问题（可以填jira地址）
     */
    @ApiModelProperty(value = "关联问题")
    private String aboutProblem;

    /**
     * 发布申请记录链接
     */
    private String publishApplyUrl;

    /**
     * 是否有数据库变更  1 有  0  没有
     */
    private Integer dbChangeFlag;

    /**
     * 环境类型 线上 线下
     */
    @ApiModelProperty(value = "发布环境类型 online线上 offline线下")
    private String envType;

    /**
     * 环境编码
     */
    @ApiModelProperty(value = "发布环境编码")
    private String envCode;

    /**
     * 环境名称
     */
    @ApiModelProperty(value = "发布环境名称")
    private String envName;

    /**
     * 发布类型 daily日常发布 emergency紧急发布
     */
    @ApiModelProperty(value = "发布类型 daily日常发布 emergency紧急发布")
    private String publishType;

    /**
     * 描述信息
     */
    @ApiModelProperty(value = "描述")
    private String publishDesc;

    /**
     * 预计发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("预计发布事件")
    private LocalDateTime expectPublishTime;

    /**
     * 变更详情
     */
    @ApiModelProperty(value = "关联变更")
    private List<ChangeOfPublishAuditDTO> changeDetails;
}
