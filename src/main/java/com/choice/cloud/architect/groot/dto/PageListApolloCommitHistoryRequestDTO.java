package com.choice.cloud.architect.groot.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * <p>
 * 分页查询Apollo配置项更改历史请求体
 * </p>
 *
 * @author LZ
 * @date Created in 2020/6/29 15:21
 */
@Data
public class PageListApolloCommitHistoryRequestDTO {
    private Integer pageNum;
    private Integer pageSize;

    @NotBlank(message = "环境名不能为空")
    private String env;

    @NotBlank(message = "应用名不能为空")
    private String appCode;

    @NotBlank(message = "集群名称不能为空")
    private String cluster;
}
