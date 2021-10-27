package com.choice.cloud.architect.groot.service.inner;

import com.choice.cloud.architect.groot.dto.ResourceSpecDTO;
import com.choice.cloud.architect.groot.request.CreateResourceSpecRequest;
import com.choice.driver.jwt.entity.AuthUser;

import java.util.List;

/**
 * @author zhangkun
 */
public interface GrResourceSpecService {
    void save(CreateResourceSpecRequest request, AuthUser authUser);

    List<ResourceSpecDTO> list();
}
