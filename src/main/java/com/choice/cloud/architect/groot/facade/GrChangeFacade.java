package com.choice.cloud.architect.groot.facade;

import com.choice.cloud.architect.groot.dto.ChangeOfPublishAuditDTO;
import com.choice.driver.jwt.entity.AuthUser;

import java.util.List;

/**
 * <p>
 * 变更单模块对外暴露能力
 * </p>
 *
 * @author zhangkun
 */
public interface GrChangeFacade {
    /**
     * 关联发布审核单
     *
     * @param publishAuditId 发布审核单id
     * @param changeIds      变更单id
     */
    void linkPublishAudit(String publishAuditId, List<String> changeIds, AuthUser authUser);

    /**
     * 取消关联发布审核单
     *
     * @param publishAuditId 发布审核单id
     * @param changeIds      变更单id
     */
    void unLinkPublishAudit(String publishAuditId, List<String> changeIds, AuthUser authUser);

    /**
     * 根据发布审核单id查询变更详情
     *
     * @param publishAuditId 发布审核单id
     * @return 关联变更单
     */
    List<ChangeOfPublishAuditDTO> queryChangeByPublishAuditId(String publishAuditId);

    /**
     * 根据变更单id查询变更单详情
     */
    ChangeOfPublishAuditDTO queryChangeById(String changeId);

    /**
     * 根据审批单id查询所有变更信息
     * @param auditId
     * @return
     */
    List<ChangeOfPublishAuditDTO> queryChangeListByAuditId(String auditId);
}
