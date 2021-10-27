package com.choice.cloud.architect.groot.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * (GrMember)实体类
 *
 * @author makejava
 * @since 2020-03-03 16:55:16
 */
@Getter
@Setter
@NoArgsConstructor
public class GrMember {
   
    /** 自增主键 **/
    private Integer id;
    /** 业务ID **/
    private String oid;
    /** 变更单ID **/
    private String changeId;
    /** 成员分类 **/
    private String memberType;
    /** 成员ID **/
    private String memberId;
    /** 成员名称 **/
    private String memberName;
    /** 联系方式 **/
    private String mobile;
    /** 状态（0:正常 1：停用） **/
    private Integer status;
    /** 删除标识(1：未删除  0：已删除) **/
    private Integer deleteFlag;
    /** 创建人 **/
    private String createUser;
    /** 创建时间 **/
    private LocalDateTime createTime;
    /** 修改人 **/
    private String updateUser;
    /** 更新时间 **/
    private LocalDateTime updateTime;
    /** 数据版本 **/
    private Integer version;

    @Builder
    public GrMember(Integer id, String oid, String changeId, String memberType, String memberId, String memberName, String mobile, Integer status, Integer deleteFlag, String createUser, LocalDateTime createTime, String updateUser, LocalDateTime updateTime, Integer version) {
        this.id = id;
        this.oid = oid;
        this.changeId = changeId;
        this.memberType = memberType;
        this.memberId = memberId;
        this.memberName = memberName;
        this.mobile = mobile;
        this.status = status;
        this.deleteFlag = deleteFlag;
        this.createUser = createUser;
        this.createTime = createTime;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.version = version;
    }
}