package com.choice.cloud.architect.groot.facade;

import com.choice.cloud.architect.groot.convertor.PublishConvertor;
import com.choice.cloud.architect.groot.dao.GrPublishMapper;
import com.choice.cloud.architect.groot.dataobject.UpdatePublishStatusDO;
import com.choice.cloud.architect.groot.dto.CreatePublishDTO;
import com.choice.cloud.architect.groot.enums.PublishStatusEnum;
import com.choice.cloud.architect.groot.model.GrPublish;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zhangkun
 */
@Slf4j
@Service
public class DefaultGrPublishFacade implements GrPublishFacade {

    @Autowired
    private GrPublishMapper grPublishMapper;

    @Override
    public GrPublish createPublish(CreatePublishDTO createPublishDTO) {
        GrPublish grPublish = new GrPublish();
        grPublish = PublishConvertor.INSTANCE.convert(createPublishDTO);

        int ret = grPublishMapper.insertDynamic(grPublish);
        log.info("DefaultGrPublishFacade createPublish 创建发布单 ret:{},grPublish:{}", ret, grPublish);
        return grPublish;
    }

    public void closePublishByChangeAndEnv(String changeId, String envType, String envCode) {
        UpdatePublishStatusDO updatePublishStatusDO = new UpdatePublishStatusDO();
        updatePublishStatusDO.setChangeId(changeId);
        updatePublishStatusDO.setEnvType(envType);
        updatePublishStatusDO.setEnvCode(envCode);
        updatePublishStatusDO.setSourcePublishStatus(PublishStatusEnum.WAIT_PUBLISH.value());
        updatePublishStatusDO.setTargetPublishStatus(PublishStatusEnum.CLOSED.value());
        grPublishMapper.updatePublishStatusByChangeIdAndEnv(updatePublishStatusDO);
    }

}
