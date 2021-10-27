package com.choice.cloud.architect.groot.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * (GrChangeRecode)实体类
 *
 * @author makejava
 * @since 2020-03-03 16:55:19
 */
@Getter
@Setter
@NoArgsConstructor
public class GrChangeRecode {

    /** 自增主键 **/
    private Integer id;
    /** 业务ID **/
    private String oid;
    /** 变更单ID **/
    private String changeId;
    /** 应用名 **/
    private String appId;
    /** 环境 **/
    private String env;
    /** 集群 **/
    private String cluster;
    /** 命名空间 **/
    private String namespace;
    /** 记录分类 APOLLO:阿波罗变更日志  DB:数据库变更 **/
    private String logType;
    /** 操作类别ADD:新增 ：UPDATE:修改  DELETE:删除 **/
    private String optType;
    /** 操作时间 **/
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime optTime;
    /** 操作前的值 **/
    private String oldValue;
    /** 操作后的值 **/
    private String newValue;
    /** 操作人 **/
    private String operator;
    /** 操作结果 **/
    private String optResult;
    /** 操作详情 **/
    private String optDetail;
    /** 状态（0:禁用、1:启用 ） **/
    private Integer status;
    /** 删除标识(1：未删除  0：已删除) **/
    private Integer deleteFlag;
    /** 创建人 **/
    private String createUser;
    /** 创建时间 **/
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    /** 修改人 **/
    private String updateUser;
    /** 更新时间 **/
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;


    @Builder
    public GrChangeRecode(Integer id, String oid, String changeId, String appId, String env, String cluster, String namespace, String logType, String optType, LocalDateTime optTime, String oldValue, String newValue, String operator, String optResult, String optDetail, Integer status, Integer deleteFlag, String createUser, LocalDateTime createTime, String updateUser, LocalDateTime updateTime) {
        this.id = id;
        this.oid = oid;
        this.changeId = changeId;
        this.appId = appId;
        this.env = env;
        this.cluster = cluster;
        this.namespace = namespace;
        this.logType = logType;
        this.optType = optType;
        this.optTime = optTime;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.operator = operator;
        this.optResult = optResult;
        this.optDetail = optDetail;
        this.status = status;
        this.deleteFlag = deleteFlag;
        this.createUser = createUser;
        this.createTime = createTime;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
    }
}
