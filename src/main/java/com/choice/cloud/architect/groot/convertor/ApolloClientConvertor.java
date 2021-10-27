package com.choice.cloud.architect.groot.convertor;

import com.choice.cloud.architect.groot.request.ApolloAppNamespaceRequest;
import com.choice.cloud.architect.groot.request.ApolloItemRequest;
import com.choice.cloud.architect.groot.request.ApolloNamespaceReleaseRequest;
import com.ctrip.framework.apollo.openapi.dto.NamespaceReleaseDTO;
import com.ctrip.framework.apollo.openapi.dto.OpenAppNamespaceDTO;
import com.ctrip.framework.apollo.openapi.dto.OpenItemDTO;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface ApolloClientConvertor {

    OpenAppNamespaceDTO namespaceReq2AppNamespace(ApolloAppNamespaceRequest apolloAppNamespaceRequest);

    OpenItemDTO itemReq2Item(ApolloItemRequest apolloItemRequest);

    NamespaceReleaseDTO namespaceReleaseReq2NamespaceRelease(ApolloNamespaceReleaseRequest apolloNamespaceReleaseRequest);

}
