package com.choice.cloud.architect.groot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ListApolloChangeDTO extends BaseBizDTO {

    private Integer id;
    /**
     * 变更单编码
     */
    private String changeId;

    private String env;

    private String cluster;

    /** 命名空间 */
    private String namespace;
    /**
     * apollo app id
     */
    private String apolloAppCode;

    /**
     * apollo变更子项key值
     */
    private String changeItemKey;

    /**
     * apollo变更子项value值
     */
    private String changeItemValue;

    /**
     * 描述信息
     */
    private String description;

    /**
     * 主键id
     */
    private String oid;

    /**
     * 状态（0：待发布，1：已发布）
     */
    private Integer status;

    /**
     * 删除标识(1：未删除 0：已删除)
     */
    private Integer deleteFlag;

    /**
     * 创建用户id
     */
    private String createUser;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新用户id
     */
    private String updateUser;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * Apollo配置项是否删除
     */
    private Boolean isDeleted;
    /**
     * Apollo配置项是否修改
     */
    private Boolean isModified;
    /**
     * 和isModified字段一起判断
     * isModified为true的话既可以是新增，也可以是修改
     * 当oldValue为空的话，是新增；否则是修改
     */
    private String oldValue;

    /**
     * 发布类型
     */
    private Integer publishType;

    @Builder
    public ListApolloChangeDTO(Integer id, String changeId, String env, String cluster, String namespace, String apolloAppCode, String changeItemKey,
        String changeItemValue, String description, String oid, Integer status, Integer deleteFlag, String createUser, LocalDateTime createTime,
        String updateUser, LocalDateTime updateTime, Boolean isDeleted, Boolean isModified, String oldValue, Integer publishType) {
        this.id = id;
        this.changeId = changeId;
        this.env = env;
        this.cluster = cluster;
        this.namespace = namespace;
        this.apolloAppCode = apolloAppCode;
        this.changeItemKey = changeItemKey;
        this.changeItemValue = changeItemValue;
        this.description = description;
        this.oid = oid;
        this.status = status;
        this.deleteFlag = deleteFlag;
        this.createUser = createUser;
        this.createTime = createTime;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.isDeleted = isDeleted;
        this.isModified = isModified;
        this.oldValue = oldValue;
        this.publishType = publishType;
    }
}
