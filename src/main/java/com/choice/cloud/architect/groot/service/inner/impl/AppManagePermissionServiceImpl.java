package com.choice.cloud.architect.groot.service.inner.impl;

import com.choice.cloud.architect.groot.exception.ServiceException;
import com.choice.cloud.architect.groot.remote.AppListResponse;
import com.choice.cloud.architect.groot.remote.AppRoleRelationDTO;
import com.choice.cloud.architect.groot.remote.enums.RoleEnum;
import com.choice.cloud.architect.groot.remote.milkyway.MilkywayService;
import com.choice.cloud.architect.groot.response.code.MilkyWayResponseCode;
import com.choice.cloud.architect.groot.service.inner.AppManagePermissionService;
import com.choice.driver.jwt.entity.AuthUser;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Author: zhangguoquan
 * @Date: 2020/8/11 17:01
 */
@Service
public class AppManagePermissionServiceImpl implements AppManagePermissionService {

    @Autowired
    private MilkywayService milkywayService;

    @Value("${permission.sre.ids}")
    private String permissionSreIds;


    @Override
    public boolean hasPermission(AuthUser authUser, String appCode) {
        AppListResponse app = milkywayService.getAppInfo(appCode);
        return hasPermission(authUser, app);
    }

    @Override
    public boolean hasPermission(AuthUser authUser, AppListResponse app) {
        Map<String, List<AppRoleRelationDTO>> appRoleMap = app.getMemberMap();
        // 应用owner
        if (authUser.getUid().equals(app.getOwnerId())) {
            return true;
        }
        // 运维人员
        if (permissionSreIds.contains(authUser.getUid())) {
            return true;
        }
        // 发布人员
        boolean appRoleEmpty = MapUtils.isEmpty(appRoleMap);
        boolean publishRoleEmpty = CollectionUtils.isEmpty(appRoleMap.get(RoleEnum.publish.getCode()));
        if (appRoleEmpty || publishRoleEmpty) {
            return false;
        }
        List<AppRoleRelationDTO> publishers = appRoleMap.get(RoleEnum.publish.getCode());
        return publishers.stream().anyMatch(p -> p.getUserId().equals(authUser.getUid()));
    }

    @Override
    public void checkPermission(AuthUser authUser, String appCode) {
        if (!hasPermission(authUser, appCode)) {
            throw new ServiceException(MilkyWayResponseCode.NO_PERMISSION);
        }
    }

    @Override
    public void checkPermission(AuthUser authUser, AppListResponse app) {
        if (!hasPermission(authUser, app)) {
            throw new ServiceException(MilkyWayResponseCode.NO_PERMISSION);
        }
    }
}
