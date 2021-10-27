package com.choice.cloud.architect.groot.service.inner;

import com.choice.cloud.architect.groot.dto.GetLinkedMemberNameByChangeIdResultDTO;
import com.choice.cloud.architect.groot.model.GrMember;
import com.choice.cloud.architect.groot.request.AddGrMemberRequest;
import com.choice.cloud.architect.groot.request.UpdateChangeMemberRequest;
import com.choice.driver.jwt.entity.AuthUser;

import java.util.List;

/**
 * (GrMember)表服务接口
 *
 * @author makejava
 * @since 2020-03-03 16:55:18
 */
public interface GrMemberService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    GrMember queryById(Object id);

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<GrMember> queryAllByLimit(int offset, int limit);

    /**
     * 新增数据
     *
     * @param request 实例对象
     * @return 实例对象
     */
    GrMember insert(AddGrMemberRequest request, AuthUser authUser);

    void updateChangeMemberBatch(UpdateChangeMemberRequest request, AuthUser authUser);

    /**
     * 修改数据
     *
     * @param grMember 实例对象
     * @return 实例对象
     */
    GrMember update(GrMember grMember);

    /**
     * 逻辑删除
     * @param oid 主键
     * @return 是否成功
     */
     void deleteByOid(String oid);

    List<GrMember> getAllRelationByMemberId(String memberId, String memberType);

    /**
     * 根据变更单id查询关联的人员信息
     * @param changeId
     */
    GetLinkedMemberNameByChangeIdResultDTO getLinkedMemberNameByChangeId(String changeId);

}