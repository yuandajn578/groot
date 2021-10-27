package com.choice.cloud.architect.groot.convertor;

import com.choice.cloud.architect.groot.dto.ListApolloChangeDTO;
import com.choice.cloud.architect.groot.model.GrApolloChangeOrder;
import com.choice.cloud.architect.groot.request.CreateApolloChangeRequest;
import com.choice.cloud.architect.groot.request.DeleteApolloChangeRequest;
import com.choice.cloud.architect.groot.request.ListApolloChangeRequest;
import com.choice.cloud.architect.groot.request.PublishApolloChangeRequest;
import com.choice.cloud.architect.groot.request.UpdateApolloChangeRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ApolloChangeConvertor {

    @Mappings({
            @Mapping(target = "namespace",constant = "application"),
            @Mapping(target = "status",constant = "0"),
            @Mapping(target = "appId",source = "apolloAppCode")
    })
    GrApolloChangeOrder createRequest2ApolloChangeOrder(CreateApolloChangeRequest createApolloChangeRequest);

    @Mappings({
            @Mapping(target = "namespace",constant = "application"),
            @Mapping(target = "status",constant = "0"),
            @Mapping(target = "appId",source = "apolloAppCode")
    })
    GrApolloChangeOrder updateRequest2ApolloChangeOrder(UpdateApolloChangeRequest updateApolloChangeRequest);

    @Mappings({
            @Mapping(target = "namespace",constant = "application"),
            @Mapping(target = "appId",source = "apolloAppCode")
    })
    GrApolloChangeOrder deleteRequest2ApolloChangeOrder(DeleteApolloChangeRequest deleteApolloChangeRequest);

    @Mappings({
            @Mapping(target = "namespace",constant = "application"),
            @Mapping(target = "appId",source = "apolloAppCode")
    })
    GrApolloChangeOrder publishRequest2ApolloChangeOrder(PublishApolloChangeRequest publishApolloChangeRequest);

    @Mappings({
            @Mapping(target = "appId",source = "appCode")
    })
    GrApolloChangeOrder pageRequest2ApolloChangeOrder(ListApolloChangeRequest listApolloChangeRequest);

    @Mappings({
            @Mapping(target = "apolloAppCode",source = "appId")
    })
    ListApolloChangeDTO pageResult2PageDto(GrApolloChangeOrder grApolloChangeOrder);
}
