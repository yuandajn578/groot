package com.choice.cloud.architect.groot.convertor;

import com.choice.cloud.architect.groot.dto.ListChangeDTO;
import com.choice.cloud.architect.groot.model.GrChange;
import com.choice.cloud.architect.groot.request.ListChangeRequest;
import com.choice.cloud.architect.groot.request.UpdateChangeRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChangeConvertor {

    ListChangeDTO change2Dto(GrChange grChange);

    GrChange request2Change(ListChangeRequest request);

    GrChange updateRequest2Change(UpdateChangeRequest updateChangeRequest);
}
