package com.choice.cloud.architect.groot.service.inner;

import java.util.List;

import com.choice.cloud.architect.groot.dto.ListApolloChangeDTO;
import com.choice.cloud.architect.groot.dto.ListApolloUpdateItemRequestDTO;
import com.choice.cloud.architect.groot.dto.ListApolloUpdateItemResponseDTO;
import com.choice.cloud.architect.groot.dto.PageListApolloCommitHistoryRequestDTO;
import com.choice.cloud.architect.groot.dto.PageListApolloCommitHistoryResponseDTO;
import com.choice.cloud.architect.groot.model.GrApolloChangeOrder;
import com.choice.cloud.architect.groot.request.CreateApolloChangeRequest;
import com.choice.cloud.architect.groot.request.DeleteApolloChangeRequest;
import com.choice.cloud.architect.groot.request.ListApolloChangeRequest;
import com.choice.cloud.architect.groot.request.PublishApolloChangeRequest;
import com.choice.cloud.architect.groot.request.UpdateApolloChangeRequest;
import com.choice.driver.jwt.entity.AuthUser;

public interface GrApolloChangeService {

    /**
     * 创建阿波罗配置变更单
     * @param createApolloChangeRequest
     */
    void createChange(CreateApolloChangeRequest createApolloChangeRequest, AuthUser authUser);

    /**
     * 创建或修改阿波罗变更单
     * @param updateApolloChangeRequest
     */
    void updateOrCreateChange(UpdateApolloChangeRequest updateApolloChangeRequest, AuthUser authUser);

    /**
     * 主键查询
     * @param oid
     */
    GrApolloChangeOrder queryChange(String oid);

    /**
     * 删除阿波罗配置
     * @param deleteApolloChangeRequest
     */
    void deleteChange(DeleteApolloChangeRequest deleteApolloChangeRequest, AuthUser authUser);


    /**
     * 发布配置
     * @param publishApolloChangeRequest
     */
    void publishChange(PublishApolloChangeRequest publishApolloChangeRequest, AuthUser authUser);

    /**
     * 阿波罗变更单查询(非分页)
     * @param listApolloChangeRequest
     * @return
     */
    List<ListApolloChangeDTO> list(ListApolloChangeRequest listApolloChangeRequest, AuthUser authUser);

    /**
     * 阿波罗修改的配置项列表集合
     * @param request
     * @return
     */
    List<ListApolloUpdateItemResponseDTO> listUpdateItem(ListApolloUpdateItemRequestDTO request);

    /**
     * 分页查询阿波罗配置项更改历史
     * @param request
     * @return
     */
    List<PageListApolloCommitHistoryResponseDTO> pageListCommitHistoryItem(PageListApolloCommitHistoryRequestDTO request);
}
