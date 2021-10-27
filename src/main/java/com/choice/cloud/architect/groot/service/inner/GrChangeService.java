package com.choice.cloud.architect.groot.service.inner;

import com.choice.cloud.architect.groot.dto.ListChangeDTO;
import com.choice.cloud.architect.groot.model.GrChange;
import com.choice.cloud.architect.groot.mq.publish.PublishSendNoticeDTO;
import com.choice.cloud.architect.groot.option.WebPage;
import com.choice.cloud.architect.groot.request.*;
import com.choice.driver.jwt.entity.AuthUser;

import java.util.List;
import java.util.Set;

/**
 * @author zhangkun
 */
public interface GrChangeService {

    /**
     * 创建变更单
     * @param createChangeRequest
     */
    String createChange(CreateChangeRequest createChangeRequest, AuthUser authUser);

    /**
     *     分页查询
     * @param listChangeRequest
     * @return
     */
    WebPage<List<ListChangeDTO>> list(ListChangeRequest listChangeRequest, AuthUser authUser);

    WebPage<List<ListChangeDTO>> list(ListChangeRequest listChangeRequest);

    /**
     * 业务主键查询
     * @param oid
     * @return
     */
    ListChangeDTO queryByOid(String oid);

    /**
     * 修改阿波罗变更单
     * @param updateChangeRequest
     */
    void updateChange(UpdateChangeRequest updateChangeRequest, AuthUser authUser);

    /**
     * 修改变更单状态
     * @param updateChangeStatusRequest
     */
    void updateStatusChange(UpdateChangeStatusRequest updateChangeStatusRequest, AuthUser authUser);

    /**
     * 验收变更单，修改状态
     * @param updateChangeStatusRequest
     * @param userName
     */
    void updateChangeTestStatus(UpdateChangeStatusRequest updateChangeStatusRequest, String userName);

    /**
     * 修改变更单生命周期状态
     * @param updateChangeStageRequest
     */
    void updateChangeStage(UpdateChangeStageRequest updateChangeStageRequest,String userName);

    /**
     * 是否存在
     * @param id
     * @return
     */
    boolean existsChange(String id);

    List<GrChange> queryByIds(Set<String> changeIds);


    List<GrChange> queryByDo(GrChange grChangeDo);

    /**
     * 针对变更单发送钉钉工作通知
     * @param sendNoticeDTO
     * @return
     */
    boolean handleChangeSendWebhookMessage(PublishSendNoticeDTO sendNoticeDTO);

}
