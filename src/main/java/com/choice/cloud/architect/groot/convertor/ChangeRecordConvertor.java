package com.choice.cloud.architect.groot.convertor;

import com.choice.cloud.architect.groot.model.GrChangeRecode;
import com.choice.cloud.architect.groot.request.ChangeRecodeRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChangeRecordConvertor {

    GrChangeRecode recordReq2ChangeRecord(ChangeRecodeRequest changeRecodeRequest);
}
