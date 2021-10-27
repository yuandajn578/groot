package com.choice.cloud.architect.groot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author zhangkun
 */
@Data
public class BaseBizDTO {
    /**
     * 主键id
     */
    @ApiModelProperty(value = "ID")
    private String oid;

    /**
     * 状态（0：关闭，1：开启）
     */
    @ApiModelProperty(value = "状态（0：关闭，1：开启）")
    private Integer status;

    /**
     * 删除标识(1：未删除 0：已删除)
     */
    @ApiModelProperty(value = "删除标识(1：未删除 0：已删除)")
    private Integer deleteFlag;

    /**
     * 创建用户id
     */
    @ApiModelProperty(value = "创建人")
    private String createUser;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新用户id
     */
    @ApiModelProperty(value = "修改人")
    private String updateUser;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "修改时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
