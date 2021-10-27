package com.choice.cloud.architect.groot.service.inner.impl;

import com.choice.cloud.architect.groot.dao.GrPublishMapper;
import com.choice.cloud.architect.groot.dto.ApplicationListByUserDTO;
import com.choice.cloud.architect.groot.enums.MemberEnum;
import com.choice.cloud.architect.groot.model.GrChange;
import com.choice.cloud.architect.groot.model.GrMember;
import com.choice.cloud.architect.groot.model.GrPublish;
import com.choice.cloud.architect.groot.option.GlobalConst;
import com.choice.cloud.architect.groot.remote.AppListResponse;
import com.choice.cloud.architect.groot.remote.milkyway.MilkywayService;
import com.choice.cloud.architect.groot.service.inner.GrApplicationService;
import com.choice.cloud.architect.groot.service.inner.GrChangeService;
import com.choice.cloud.architect.groot.service.inner.GrMemberService;
import com.choice.driver.jwt.entity.AuthUser;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/19 14:10
 */
@Service
public class GrApplicationServiceImpl implements GrApplicationService {

    @Autowired
    private MilkywayService milkywayService;

    @Autowired
    private GrPublishMapper grPublishMapper;

    @Autowired
    private GrMemberService grMemberService;

    @Autowired
    private GrChangeService grChangeService;

    @Override
    public List<ApplicationListByUserDTO> getAppListByUser(AuthUser authUser) {

        // 获取用户关联变更
//        List<GrMember> grMemberList = grMemberService.getAllRelationByMemberId(authUser.getUid(), MemberEnum.DEVELOPER.name());
//        Set<String> appCodeSet = Sets.newHashSet();
//        if (CollectionUtils.isNotEmpty(grMemberList)) {
//            //获取变更中的appCode Set
//            List<GrChange> grChangeList = grChangeService
//                    .queryByIds(grMemberList.stream().map(GrMember::getChangeId).collect(Collectors.toSet()));
//            appCodeSet = grChangeList.stream().map(GrChange::getAppCode).collect(Collectors.toSet());
//        }
//
//        GrChange queryDo = new GrChange();
//        queryDo.setOperatorId(authUser.getUid());
//        queryDo.setDeleteFlag(GlobalConst.NOT_DELETE);
//        List<GrChange> grChangeList = grChangeService.queryByDo(queryDo);
//        if (CollectionUtils.isNotEmpty(grChangeList)) {
//            appCodeSet.addAll(
//                    grChangeList.stream().map(GrChange::getAppCode).collect(Collectors.toSet())
//            );
//        }
//
//        // 分别查询用户下app  通过变更获取的appCode查询出的app
//        List<AppListResponse> appListByUser = milkywayService.getAppListByUser(authUser.getUid());
//        if (CollectionUtils.isNotEmpty(appCodeSet)) {
//            List<AppListResponse> appListByCode = milkywayService.getAppListByCodes(appCodeSet);
//            appListByUser.addAll(appListByCode);
//
//            //取并集
//            final Map<String, Boolean> keyMap = new ConcurrentHashMap<>();
//            appListByUser = appListByUser.stream().filter(app ->
//                    keyMap.putIfAbsent(app.getAppCode(), Boolean.TRUE) == null
//            ).collect(Collectors.toList());
//        }

        List<AppListResponse> appListByUser = milkywayService.getAppList();

        // 查询该用户最后一次发布
        GrPublish grPublish = grPublishMapper.lastByOperatorId(authUser.getUid(), GlobalConst.NOT_DELETE);
        if (grPublish != null) {
            String lastDeployAppCode = grPublish.getAppCode();
            int lastDeployAppIndex = 0;

            for (int i = 0; i < appListByUser.size(); i++) {
                if(appListByUser.get(i).getAppCode().equals(lastDeployAppCode)) {
                    lastDeployAppIndex = i;
                    break;
                }
            }

            if (lastDeployAppIndex > 0) {
                // 将最后一次发布的应用交换至最前
                Collections.swap(appListByUser, 0 , lastDeployAppIndex);
            }
        }

        return appListByUser.stream().map(appListResponse -> {
            ApplicationListByUserDTO applicationListByUserDTO = new ApplicationListByUserDTO();
            BeanUtils.copyProperties(appListResponse, applicationListByUserDTO);
            return applicationListByUserDTO;
        }).collect(Collectors.toList());
    }
}
