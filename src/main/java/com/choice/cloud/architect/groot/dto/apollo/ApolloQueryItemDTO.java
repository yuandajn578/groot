package com.choice.cloud.architect.groot.dto.apollo;

import lombok.Data;

/**
 * <p>
 * 根据环境、集群和命名空间获取该条件下所有的配置项实体
 * </p>
 *
 * @author LZ
 * @date Created in 2020/5/25 16:40
 */
@Data
public class ApolloQueryItemDTO {
    private String comment;
    private Integer id;
    private String key;
    private Integer lineNum;
    private Integer namespaceId;
    private String value;
}
