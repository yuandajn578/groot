package com.choice.cloud.architect.groot.service.inner;

import com.choice.cloud.architect.groot.dto.ListIdcLdcDTO;
import com.choice.cloud.architect.groot.dto.ListLdcInfoDTO;

import java.util.List;

/**
 * @author zhangkun
 */
public interface LdcPublishService {
    /**
     * 随机获取一个LDC
     *
     * @param envType 环境类型
     * @param idc     idc
     * @return 用于发布的LDC
     */
    String randomLdc(String envType, String idc);

    List<ListIdcLdcDTO> listLdcInfo();
}
