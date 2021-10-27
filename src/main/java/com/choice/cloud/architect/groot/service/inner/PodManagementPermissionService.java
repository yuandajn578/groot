package com.choice.cloud.architect.groot.service.inner;

import com.choice.driver.jwt.entity.AuthUser;

/**
 * @author zhangkun
 */
public interface PodManagementPermissionService {
    /**
     * 是否有权限进行Pod相关操作
     *
     * @param authUser 当前操作人员
     * @return true or false
     */
    boolean hasPermission(AuthUser authUser);
}
