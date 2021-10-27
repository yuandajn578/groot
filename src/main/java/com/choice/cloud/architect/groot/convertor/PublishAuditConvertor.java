package com.choice.cloud.architect.groot.convertor;

import com.choice.cloud.architect.groot.dto.ListPublishAuditDTO;
import com.choice.cloud.architect.groot.dto.PublishAuditDetailDTO;
import com.choice.cloud.architect.groot.model.GrPublishAudit;
import com.choice.cloud.architect.groot.request.CreatePublishAuditRequest;
import com.choice.cloud.architect.groot.request.EditPublishAuditRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author zhangkun
 */
@Mapper(componentModel = "spring")
public interface PublishAuditConvertor {
    PublishAuditConvertor INSTANCE = Mappers.getMapper(PublishAuditConvertor.class);

    GrPublishAudit convert(CreatePublishAuditRequest request);

    ListPublishAuditDTO convert(GrPublishAudit grPublishAudit);

    PublishAuditDetailDTO convertDetail(GrPublishAudit grPublishAudit);

    GrPublishAudit convert(EditPublishAuditRequest request);
}
