package com.choice.cloud.architect.groot.service.inner;

import com.choice.cloud.architect.groot.remote.AppListResponse;
import com.choice.driver.jwt.entity.AuthUser;

/**
 * @Author: zhangguoquan
 * @Date: 2020/8/11 16:59
 */
public interface AppManagePermissionService {

    boolean hasPermission(AuthUser authUser, String appCode);

    boolean hasPermission(AuthUser authUser, AppListResponse app);

    void checkPermission(AuthUser authUser, String appCode);

    void checkPermission(AuthUser authUser, AppListResponse app);
}
