package com.choice.cloud.architect.groot.service.inner;

import com.choice.cloud.architect.groot.dto.AuditDTalkProcessListRequestDTO;
import com.choice.cloud.architect.groot.dto.AuditDTalkProcessListResponseDTO;
import com.choice.cloud.architect.groot.dto.ListPublishAuditDTO;
import com.choice.cloud.architect.groot.dto.PublishAuditDetailDTO;
import com.choice.cloud.architect.groot.model.GrPublishAudit;
import com.choice.cloud.architect.groot.request.CreatePublishAuditRequest;
import com.choice.cloud.architect.groot.request.EditPublishAuditRequest;
import com.choice.cloud.architect.groot.request.ListPublishAuditRequest;
import com.choice.cloud.architect.groot.response.ResponseData;
import com.choice.driver.jwt.entity.AuthUser;

import java.util.List;

/**
 * @author zhangkun
 */
public interface GrPublishAuditService {
    GrPublishAudit createPublishAudit(CreatePublishAuditRequest request, AuthUser authUser);

    ResponseData<List<ListPublishAuditDTO>> pageQueryPublishAudit(ListPublishAuditRequest request);

    PublishAuditDetailDTO getPublishAuditDetail(String oid);

    void submitPublishAudit(String oid, AuthUser authUser);

    void cancelPublishAudit(String oid);

    void editPublishAudit(EditPublishAuditRequest request, AuthUser authUser);

    void createAndSubmitPublishAudit(CreatePublishAuditRequest request, AuthUser authUser);

    void linkChange(String changeId, String publishAuditId, AuthUser authUser);

    /**
     * 通过审核 更改审核单状态，并创建发布单
     * @param dTalkProcessId
     */
    void approvedProcess(String dTalkProcessId);

    /**
     * 拒绝审核单
     * @param dTalkProcessId
     */
    void refuseProcess(String dTalkProcessId) ;

    /**
     * 撤回审核单
     * @param dTalkProcessId
     */
    void withdrawProcess(String dTalkProcessId) ;

    void createPublish(GrPublishAudit grPublishAudit);

    GrPublishAudit getLastAuditByChangeIdAndEnv(String changeId, String envType, String envCode);

    /**
     * 根据钉钉审核单id查询审批进度
     * @param request
     * @return
     */
    AuditDTalkProcessListResponseDTO getProcessListByDTalkId(AuditDTalkProcessListRequestDTO request);
}
