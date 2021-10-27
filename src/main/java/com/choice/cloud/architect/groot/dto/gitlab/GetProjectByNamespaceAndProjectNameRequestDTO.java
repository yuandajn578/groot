package com.choice.cloud.architect.groot.dto.gitlab;

import lombok.Data;

/**
 * <p>
 * 根据GitLab的namespace和项目名称查询工程请求体
 * </p>
 *
 * @author LZ
 * @date Created in 2020/7/24 15:57
 */
@Data
public class GetProjectByNamespaceAndProjectNameRequestDTO {
    private String namespace;
    private String projectName;
}
