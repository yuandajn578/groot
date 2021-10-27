package com.choice.cloud.architect.groot.service.inner.impl;

import com.choice.cloud.architect.groot.dao.GrMemberMapper;
import com.choice.cloud.architect.groot.dto.GetLinkedMemberNameByChangeIdResultDTO;
import com.choice.cloud.architect.groot.enums.MemberEnum;
import com.choice.cloud.architect.groot.exception.ServiceException;
import com.choice.cloud.architect.groot.model.GrMember;
import com.choice.cloud.architect.groot.option.GlobalConst;
import com.choice.cloud.architect.groot.request.AddGrMemberRequest;
import com.choice.cloud.architect.groot.request.CreateChangeRequest;
import com.choice.cloud.architect.groot.request.CreateMemberRequest;
import com.choice.cloud.architect.groot.request.UpdateChangeMemberRequest;
import com.choice.cloud.architect.groot.response.code.ChangeResponseCode;
import com.choice.cloud.architect.groot.service.inner.GrMemberService;
import com.choice.cloud.architect.groot.util.CommonUtil;
import com.choice.cloud.architect.groot.util.UserHolder;
import com.choice.driver.jwt.entity.AuthUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * (GrMember)表服务实现类
 *
 * @author makejava
 * @since 2020-03-03 16:55:18
 */
@Slf4j
@Service("grMemberService")
public class GrMemberServiceImpl implements GrMemberService {

    @Resource
    private GrMemberMapper grMemberDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public GrMember queryById(Object id) {
        return this.grMemberDao.queryById(id);
    }

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    @Override
    public List<GrMember> queryAllByLimit(int offset, int limit) {
        return this.grMemberDao.queryAllByLimit(offset, limit);
    }

    /**
     * 新增数据
     *
     * @param request 实例对象
     * @return 实例对象
     */
    @Override
    public GrMember insert(AddGrMemberRequest request, AuthUser authUser) {
        GrMember grMember = new GrMember();
        BeanUtils.copyProperties(request, grMember);
        grMember.setOid(CommonUtil.getUUIdUseMongo());
        grMember.setDeleteFlag(GlobalConst.NOT_DELETE);
        grMember.setCreateTime(LocalDateTime.now());
        grMember.setUpdateTime(LocalDateTime.now());
        grMember.setStatus(1);
        grMember.setCreateUser(UserHolder.getUserName(authUser.getUid()));
        grMember.setUpdateUser(UserHolder.getUserName(authUser.getUid()));
        this.grMemberDao.insert(grMember);
        return grMember;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateChangeMemberBatch(UpdateChangeMemberRequest request, AuthUser authUser) {

        String changeId = request.getChangeId();
        String memberType = request.getMemberType();
        // 删除所有
        grMemberDao.deleteByChangeIdAndMemberType(changeId, memberType, GlobalConst.DELETE);
        // 全部新增
        if (CollectionUtils.isEmpty(request.getMemberList())) {
            return;
        }
        List<GrMember> grMemberList = Lists.newArrayList();
        checkCrMembers(request);
        for (AddGrMemberRequest member: request.getMemberList()) {
            GrMember grMember = new GrMember();
            grMember.setChangeId(changeId);
            grMember.setMemberType(memberType);
            grMember.setMemberId(member.getMemberId());
            grMember.setMemberName(member.getMemberName());
            grMember.setOid(CommonUtil.getUUIdUseMongo());
            grMember.setStatus(1);
            grMember.setDeleteFlag(GlobalConst.NOT_DELETE);
            grMember.setCreateTime(LocalDateTime.now());
            grMember.setUpdateTime(LocalDateTime.now());
            grMember.setCreateUser(UserHolder.getUserName(authUser.getUid()));
            grMember.setUpdateUser(UserHolder.getUserName(authUser.getUid()));
            grMemberList.add(grMember);
        }
        grMemberDao.batchInsert(grMemberList);
    }

    private void checkCrMembers(UpdateChangeMemberRequest updateChangeMemberRequest) {
        List<AddGrMemberRequest> members = updateChangeMemberRequest.getMemberList();
        Map<String, List<AddGrMemberRequest>> membersMap = members.stream()
                .collect(Collectors.groupingBy(AddGrMemberRequest::getMemberType));

        List<AddGrMemberRequest> crMembers = membersMap.get(MemberEnum.CODEREVIEW.name());
        List<AddGrMemberRequest> devMembers = membersMap.get(MemberEnum.DEVELOPER.name());
        if (CollectionUtils.isNotEmpty(crMembers) && CollectionUtils.isNotEmpty(devMembers)) {
            if (crMembers.size() == 1) {
                if(devMembers.stream().anyMatch(m -> m.getMemberId().equals(crMembers.get(0).getMemberId()))) {
                    throw new ServiceException(ChangeResponseCode.CR_DEV_MEMBER_SAME);
                }
            }
        }

    }

    /**
     * 修改数据
     *
     * @param grMember 实例对象
     * @return 实例对象
     */
    @Override
    public GrMember update(GrMember grMember) {
        this.grMemberDao.update(grMember);
        return this.queryById(grMember.getId());
    }

    /**
     * 主键逻辑删除
     * @param oid 主键
     * @return 是否成功
     */
    @Override
    public void deleteByOid(String oid) {
        grMemberDao.update(GrMember.builder().oid(oid).deleteFlag(GlobalConst.DELETE).build());
    }

    @Override
    public List<GrMember> getAllRelationByMemberId(String memberId, String memberType){
        return grMemberDao.listByMemberIdAndType(memberId, memberType, GlobalConst.NOT_DELETE);
    }

    /**
     * 根据变更单id查询关联的人员信息
     * @param changeId
     */
    @Override
    public GetLinkedMemberNameByChangeIdResultDTO getLinkedMemberNameByChangeId(String changeId) {
        List<GrMember> memberList = grMemberDao.queryByChangeId(changeId);
        GetLinkedMemberNameByChangeIdResultDTO resultDTO = new GetLinkedMemberNameByChangeIdResultDTO();
        if (CollectionUtils.isEmpty(memberList)) {
            log.error("根据变更单id查询关联的人员信息，对应变更单id：{}", changeId);
        } else {
            resultDTO.setDevelopmentMembers(memberList.stream().filter(Objects::nonNull).filter(member -> MemberEnum.DEVELOPER.name().equalsIgnoreCase(member.getMemberType())).map(GrMember::getMemberName).collect(Collectors.toList()));
            resultDTO.setTestMembers(memberList.stream().filter(Objects::nonNull).filter(member -> MemberEnum.CONNER.name().equalsIgnoreCase(member.getMemberType())).map(GrMember::getMemberName).collect(Collectors.toList()));
            resultDTO.setCrMembers(memberList.stream().filter(Objects::nonNull).filter(member -> MemberEnum.CODEREVIEW.name().equalsIgnoreCase(member.getMemberType())).map(GrMember::getMemberName).collect(Collectors.toList()));
        }
        return resultDTO;
    }
}