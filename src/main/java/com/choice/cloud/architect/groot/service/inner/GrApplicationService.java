package com.choice.cloud.architect.groot.service.inner;

import com.choice.cloud.architect.groot.dto.ApplicationListByUserDTO;
import com.choice.driver.jwt.entity.AuthUser;

import java.util.List;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/19 14:02
 */
public interface GrApplicationService {

    List<ApplicationListByUserDTO> getAppListByUser(AuthUser authUser);

}
