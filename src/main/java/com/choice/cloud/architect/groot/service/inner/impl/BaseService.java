package com.choice.cloud.architect.groot.service.inner.impl;

import com.choice.cloud.architect.groot.model.BaseBizModel;
import com.choice.cloud.architect.groot.option.GlobalConst;
import com.choice.cloud.architect.groot.util.CommonUtil;
import com.choice.cloud.architect.groot.util.UserHolder;
import com.choice.driver.jwt.entity.AuthUser;

import java.time.LocalDateTime;

/**
 * @author zhangkun
 */
public class BaseService {
    public String currentUserName(AuthUser authUser) {
        return UserHolder.getUserName(authUser.getUid());
    }

    public String currentUserName(String userId) {
        return UserHolder.getUserName(userId);
    }

    public void initSaveModel(BaseBizModel baseBizModel, AuthUser authUser) {
        baseBizModel.setOid(CommonUtil.getUUIdUseMongo());
        baseBizModel.setDeleteFlag(GlobalConst.NOT_DELETE);
        baseBizModel.setCreateTime(LocalDateTime.now());
        baseBizModel.setUpdateTime(LocalDateTime.now());
        baseBizModel.setCreateUser(UserHolder.getUserName(authUser.getUid()));
        baseBizModel.setUpdateUser(UserHolder.getUserName(authUser.getUid()));
    }

    public String getUserName(AuthUser authUser) {
        return UserHolder.getUserName(authUser.getUid());
    }

    public void initSaveModel(BaseBizModel baseBizModel, String userId) {
        baseBizModel.setOid(CommonUtil.getUUIdUseMongo());
        baseBizModel.setDeleteFlag(GlobalConst.NOT_DELETE);
        baseBizModel.setCreateTime(LocalDateTime.now());
        baseBizModel.setUpdateTime(LocalDateTime.now());
        baseBizModel.setCreateUser(UserHolder.getUserName(userId));
        baseBizModel.setUpdateUser(UserHolder.getUserName(userId));
    }
}
