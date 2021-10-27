package com.choice.cloud.architect.groot.service.inner;

import com.choice.cloud.architect.groot.dto.ListPublishDetailDTO;
import com.choice.cloud.architect.groot.dto.PublishDetailCreateDTO;
import com.choice.driver.jwt.entity.AuthUser;

import java.util.List;

/**
 * @author zhangkun
 */
public interface GrPublishDetailService {
    /**
     * 保存发布单详情信息
     *
     * @param publishDetailCreateDTO 发布Pod等信息
     * @param authUser               当前操作用户
     */
    void savePublishDetail(PublishDetailCreateDTO publishDetailCreateDTO, AuthUser authUser);

    /**
     * 根据发布单查询发布详情信息
     *
     * @param publishId 发布单id
     * @return 发布单详情信息
     */
    List<ListPublishDetailDTO> listPublishDetail(String publishId);
}
