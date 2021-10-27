package com.choice.cloud.architect.groot.service.inner.impl;

import com.choice.cloud.architect.groot.properties.PodOperatorWhiteListProperties;
import com.choice.cloud.architect.groot.service.inner.PodManagementPermissionService;
import com.choice.driver.jwt.entity.AuthUser;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zhangkun
 */
@Service
public class PodManagementPermissionServiceImpl extends BaseService implements PodManagementPermissionService {
    @Autowired
    private PodOperatorWhiteListProperties podOperatorWhiteListProperties;

    @Override
    public boolean hasPermission(AuthUser authUser) {
        // 权限检查
        String currentUserName = currentUserName(authUser);
        String whiteListData = podOperatorWhiteListProperties.getWhiteList();

        if (StringUtils.isBlank(whiteListData)) {
            return true;
        }

        boolean hasPermission = Splitter.on(",").splitToList(whiteListData).contains(currentUserName);

        return hasPermission;
    }
}
