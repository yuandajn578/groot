package com.choice.cloud.architect.groot.facade;

import com.choice.cloud.architect.groot.dao.GrChangeMapper;
import com.choice.cloud.architect.groot.dao.GrMemberMapper;
import com.choice.cloud.architect.groot.dao.GrPublishAuditChangeRelMapper;
import com.choice.cloud.architect.groot.dataobject.UnbindPublishAuditChangeRelDO;
import com.choice.cloud.architect.groot.dto.ChangeOfPublishAuditDTO;
import com.choice.cloud.architect.groot.enums.MemberEnum;
import com.choice.cloud.architect.groot.exception.ServiceException;
import com.choice.cloud.architect.groot.model.GrChange;
import com.choice.cloud.architect.groot.model.GrMember;
import com.choice.cloud.architect.groot.model.GrPublishAuditChangeRel;
import com.choice.cloud.architect.groot.option.GlobalConst;
import com.choice.cloud.architect.groot.response.code.PublishAuditResponseCode;
import com.choice.cloud.architect.groot.response.code.PublishResponseCode;
import com.choice.cloud.architect.groot.response.code.SysResponseCode;
import com.choice.cloud.architect.groot.service.inner.impl.BaseService;
import com.choice.cloud.architect.groot.util.CommonUtil;
import com.choice.driver.jwt.entity.AuthUser;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.omg.CORBA.SystemException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 变更单模块对外暴露能力
 * </p>
 *
 * @author zhangkun
 */
@Service
@Slf4j
public class DefaultGrChangeFacade extends BaseService implements GrChangeFacade {
    @Autowired
    private GrChangeMapper grChangeMapper;

    @Autowired
    private GrMemberMapper grMemberMapper;

    @Autowired
    private GrPublishAuditChangeRelMapper grPublishAuditChangeRelMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void linkPublishAudit(String publishAuditId, List<String> changeIds, AuthUser authUser) {
        if (publishAuditId == null || changeIds == null) {
            throw new ServiceException(SysResponseCode.PARAM_ILLEGAL);
        }

        List<GrChange> changeList = grChangeMapper.selectByIds(changeIds);
        Map<String, Long> collect =
                changeList.stream().collect(Collectors.groupingBy(GrChange::getAppCode, Collectors.counting()));
        for (Long value : collect.values()) {
            if (value > 1) {
                throw new ServiceException(PublishAuditResponseCode.SAME_APP_CHANGE);
            }
        }

        for (String changeId : changeIds) {
            GrPublishAuditChangeRel grPublishAuditChangeRel = new GrPublishAuditChangeRel();
            initSaveModel(grPublishAuditChangeRel, authUser);
            grPublishAuditChangeRel.setChangeId(changeId);
            grPublishAuditChangeRel.setPublishAuditId(publishAuditId);

            grPublishAuditChangeRelMapper.insert(grPublishAuditChangeRel);
        }

        log.info("DefaultGrChangeFacade linkPublishAudit 关联变更单成功 publishAuditId:{},changeId:{}", publishAuditId, changeIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unLinkPublishAudit(String publishAuditId, List<String> changeIds, AuthUser authUser) {
        if (publishAuditId == null || changeIds == null) {
            throw new ServiceException(SysResponseCode.PARAM_ILLEGAL);
        }

        for (String changeId : changeIds) {
            UnbindPublishAuditChangeRelDO unbindPublishAuditChangeRelDO = new UnbindPublishAuditChangeRelDO();
            unbindPublishAuditChangeRelDO.setChangeId(changeId);
            unbindPublishAuditChangeRelDO.setPublishAuditId(publishAuditId);
            unbindPublishAuditChangeRelDO.setUpdateUser(currentUserName(authUser));
            unbindPublishAuditChangeRelDO.setDeleteFlag(GlobalConst.DELETE);

            int rows = grPublishAuditChangeRelMapper.unbindRel(unbindPublishAuditChangeRelDO);

            if (rows == 0) {
                log.error("DefaultGrChangeFacade linkPublishAudit 解绑变更单失败，变更单没有被此审核单被关联，不能解绑 publishAuditId:{},changeId:{}", publishAuditId, changeId);
                throw new ServiceException(PublishResponseCode.CHANGE_NO_BIND);
            }
        }

        log.info("DefaultGrChangeFacade linkPublishAudit 解绑变更单成功 publishAuditId:{},changeId:{}", publishAuditId, changeIds);
    }

    @Override
    public List<ChangeOfPublishAuditDTO> queryChangeByPublishAuditId(String publishAuditId) {
        List<ChangeOfPublishAuditDTO> changeOfPublishAuditDTOList = Lists.newArrayList();

        List<GrChange> grChangeList = getGrChanges(publishAuditId);

        if (CollectionUtils.isEmpty(grChangeList)) {
            return changeOfPublishAuditDTOList;
        }

        for (GrChange change : grChangeList) {
            ChangeOfPublishAuditDTO changeOfPublishAuditDTO = new ChangeOfPublishAuditDTO();
            BeanUtils.copyProperties(change, changeOfPublishAuditDTO);

            // 查询开发
//            changeOfPublishAuditDTO.setDeveloper(getDeveloperName(change.getOid()));
//            changeOfPublishAuditDTO.setTester(getTesterName(change.getOid()));
            changeOfPublishAuditDTO.setGitRepo(change.getGitlabAddress());
            changeOfPublishAuditDTO.setGitBranch(change.getBranchName());
            changeOfPublishAuditDTO.setStatus(change.getStatus());
            changeOfPublishAuditDTO.setCreateTime(change.getCreateTime());
            changeOfPublishAuditDTO.setUpdateTime(change.getUpdateTime());

            changeOfPublishAuditDTOList.add(changeOfPublishAuditDTO);
        }

        return changeOfPublishAuditDTOList;
    }

    @Override
    public ChangeOfPublishAuditDTO queryChangeById(String changeId) {
        ChangeOfPublishAuditDTO changeOfPublishAuditDTO = new ChangeOfPublishAuditDTO();

        GrChange change = grChangeMapper.selectById(changeId);
        BeanUtils.copyProperties(change, changeOfPublishAuditDTO);
        // 查询开发人员
        changeOfPublishAuditDTO.setDeveloper(getDeveloperName(change.getOid()));
        // 查询测试人员
        changeOfPublishAuditDTO.setTester(getTesterName(change.getOid()));

        return changeOfPublishAuditDTO;
    }

    /**
     * 根据审核单id查询变更列表
     * @param auditId
     * @return
     */
    @Override
    public List<ChangeOfPublishAuditDTO> queryChangeListByAuditId(String auditId) {
        List<ChangeOfPublishAuditDTO> changeDTOList = Lists.newArrayList();

        List<GrChange> grChanges = getGrChanges(auditId);

        if (CollectionUtils.isNotEmpty(grChanges)) {
            for (GrChange change : grChanges) {
                ChangeOfPublishAuditDTO changeDTO = new ChangeOfPublishAuditDTO();
                BeanUtils.copyProperties(change, changeDTO);
                changeDTO.setGitRepo(change.getGitlabAddress());
                changeDTO.setGitBranch(change.getBranchName());
                changeDTO.setCrMembers(change.getCrMembers());
                changeDTOList.add(changeDTO);
            }
        }

        return changeDTOList;
    }

    private String getDeveloperName(String changeId) {
        // 查询开发
        List<GrMember> developerList = grMemberMapper.listByChangeIdAndType(changeId, "DEVELOPER", GlobalConst.NOT_DELETE);
        List<String> developerNameList = developerList.stream().map(GrMember::getMemberName)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(developerNameList)) {
            return Joiner.on(",").join(developerNameList.iterator());
        }

        return null;
    }

    private String getTesterName(String changeId) {
        List<GrMember> testerList = grMemberMapper.listByChangeIdAndType(changeId, "CONNER", GlobalConst.NOT_DELETE);
        List<String> testerNameList = testerList.stream().map(GrMember::getMemberName)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(testerNameList)) {
            return Joiner.on(",").join(testerNameList.iterator());
        }

        return null;
    }

    private List<GrChange> getGrChanges(String publishAuditId) {
        List<GrPublishAuditChangeRel> publishAuditChangeRelList = grPublishAuditChangeRelMapper.getByPublishAuditId(publishAuditId);

        if (CollectionUtils.isEmpty(publishAuditChangeRelList)) {
            log.info("发布审核单[{}]查询无变更单", publishAuditId);
            return Collections.emptyList();
        }

        List<String> changeIds = publishAuditChangeRelList.stream()
                .map(GrPublishAuditChangeRel::getChangeId)
                .collect(Collectors.toList());

        List<GrMember> developerMembers = grMemberMapper.queryByChangeIds(MemberEnum.DEVELOPER.name(),changeIds, GlobalConst.NOT_DELETE);
        List<GrMember> connerMembers = grMemberMapper.queryByChangeIds(MemberEnum.CONNER.name(),changeIds, GlobalConst.NOT_DELETE);
        List<GrMember> crMembers = grMemberMapper.queryByChangeIds(MemberEnum.CODEREVIEW.name(),changeIds, GlobalConst.NOT_DELETE);

        Map<String, List<GrMember>> developerMemberMap = developerMembers.stream().collect(Collectors.groupingBy(GrMember::getChangeId));
        Map<String, List<GrMember>> connerMemberMap = connerMembers.stream().collect(Collectors.groupingBy(GrMember::getChangeId));
        Map<String, List<GrMember>> crMemberMap = crMembers.stream().collect(Collectors.groupingBy(GrMember::getChangeId));

        Map<String, String> conMemberMap = Maps.newHashMap();
        connerMemberMap.forEach((k,v) -> {
            StringBuilder conners = new StringBuilder();
            if(CollectionUtils.isNotEmpty(v)){
                for (GrMember grMember:v) {
                    conners.append(grMember.getMemberName()).append(",");
                }
            }
            conMemberMap.put(k,conners.length()>1?conners.substring(0,conners.length()-1):conners.toString());
        });

        Map<String, String> devMemberMap = Maps.newHashMap();
        developerMemberMap.forEach((k,v) -> {
            StringBuilder developers = new StringBuilder();
            if(CollectionUtils.isNotEmpty(v)){
                for (GrMember grMember:v) {
                    developers.append(grMember.getMemberName()).append(",");
                }
            }
            devMemberMap.put(k,developers.length()>1?developers.substring(0,developers.length()-1):developers.toString());
        });


        List<GrChange> grChangeList = Lists.newArrayList();
        for (String changeId : changeIds) {
            GrChange grChange = grChangeMapper.selectById(changeId);
            grChange.setDeveloper(devMemberMap.get(grChange.getOid()));
            grChange.setTester(conMemberMap.get(grChange.getOid()));
            grChange.setCrMembers(crMemberMap.get(grChange.getOid()));
            grChangeList.add(grChange);
        }

        return grChangeList;
    }
}
