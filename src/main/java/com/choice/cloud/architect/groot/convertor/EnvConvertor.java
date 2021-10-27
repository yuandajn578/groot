package com.choice.cloud.architect.groot.convertor;

import com.choice.cloud.architect.groot.dataobject.EnvQueryPageDO;
import com.choice.cloud.architect.groot.dto.SelectListEnvDTO;
import com.choice.cloud.architect.groot.model.GrEnv;
import com.choice.cloud.architect.groot.request.ListEnvRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author zhangkun
 */
@Mapper(componentModel = "spring")
public interface EnvConvertor {
    EnvConvertor INSTANCE = Mappers.getMapper(EnvConvertor.class);

    EnvQueryPageDO convert(ListEnvRequest request);

    SelectListEnvDTO convert(GrEnv grEnv);
}
