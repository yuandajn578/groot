package com.choice.cloud.architect.groot.dto.apollo;

import java.util.List;

import lombok.Data;

/**
 * <p>
 * 根据环境、集群和命名空间获取该条件下所有的配置项响应体
 * </p>
 *
 * @author LZ
 * @date Created in 2020/5/25 16:28
 */
@Data
public class ApolloQueryAllItemByEnvIdcNamespaceResponseDTO {

    /**
     * 封装配置项结果集合对象
     */
    private List<ApolloQueryAllItemByEnvIdcNamespaceResponseDTO> items;

    private Boolean isDeleted;
    private Boolean isModified;
    private String oldValue;
    private String newValue;
    private ApolloQueryItemDTO item;
}
