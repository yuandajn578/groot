package com.choice.cloud.architect.groot.dto.apollo;

import lombok.Data;

import java.util.List;

/**
 * @ClassName ApolloBasicDTO
 * @Description TODO
 * @Author Guangshan Wang
 * @Date 2020/7/23/023 15:53
 */
@Data
public class ApolloBasicDTO {
    String appId;
    String env;
    String clusterName;
    List<String> namespacesList;
}
