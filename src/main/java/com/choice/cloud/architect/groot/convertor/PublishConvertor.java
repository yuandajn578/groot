package com.choice.cloud.architect.groot.convertor;

import com.choice.cloud.architect.groot.dto.CreatePublishDTO;
import com.choice.cloud.architect.groot.model.GrPublish;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author zhangkun
 */
@Mapper(componentModel = "spring")
public interface PublishConvertor {
    PublishConvertor INSTANCE = Mappers.getMapper(PublishConvertor.class);

    GrPublish convert(CreatePublishDTO createPublishDTO);
}
