package com.choice.cloud.architect.groot.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * <p>
 * 已修改的Apollo配置项集合请求体
 * </p>
 *
 * @author LZ
 * @date Created in 2020/6/15 20:35
 */
@Data
public class ListApolloUpdateItemRequestDTO {
    @NotBlank(message = "环境名不能为空")
    private String env;

    @NotBlank(message = "应用名不能为空")
    private String appCode;

    @NotBlank(message = "集群名称不能为空")
    private String cluster;
}
