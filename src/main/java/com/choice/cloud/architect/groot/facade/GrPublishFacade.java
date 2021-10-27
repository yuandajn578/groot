package com.choice.cloud.architect.groot.facade;

import com.choice.cloud.architect.groot.dto.CreatePublishDTO;
import com.choice.cloud.architect.groot.model.GrPublish;

/**
 * <p>
 * 发布单模块对外暴露能力
 * </p>
 *
 * @author zhangkun
 */
public interface GrPublishFacade {
    /**
     * 创建发布单
     *
     * @param createPublishDTO 发布单数据
     */
    GrPublish createPublish(CreatePublishDTO createPublishDTO);

    /**
     * 根据变更及环境关闭  待发布状态的 发布单
     * @param changeId
     * @param envType
     * @param envCode
     */
    void closePublishByChangeAndEnv(String changeId, String envType, String envCode);
}
