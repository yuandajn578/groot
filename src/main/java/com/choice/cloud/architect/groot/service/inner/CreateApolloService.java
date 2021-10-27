package com.choice.cloud.architect.groot.service.inner;

import com.choice.cloud.architect.groot.dto.CreateAppIdRequest;

/**
 * @ClassName CreateApolloService
 * @Description 通过Cookie远程调用Apollo相关接口
 * @Author LZ
 * @Date 2020/4/13 15:44
 * @Version 1.0
 */
public interface CreateApolloService {
    /**
     * 创建AppId和idc
     *
     * @param request
     */
    void createAppIdAndIdc(CreateAppIdRequest request);
}
