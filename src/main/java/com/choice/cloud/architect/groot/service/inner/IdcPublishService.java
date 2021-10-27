package com.choice.cloud.architect.groot.service.inner;

import com.choice.cloud.architect.groot.dto.ListIdcInfoDTO;

import java.util.Set;

/**
 * @author zhangkun
 */
public interface IdcPublishService {
    /**
     * 获取idc列表信息
     *
     * @return idc列表信息
     */
    Set<ListIdcInfoDTO> listIdcInfo();
}
