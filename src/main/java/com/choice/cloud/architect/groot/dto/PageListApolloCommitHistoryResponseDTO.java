package com.choice.cloud.architect.groot.dto;

import lombok.Data;

/**
 * <p>
 * 分页查询Apollo配置项更改历史响应体
 * </p>
 *
 * @author LZ
 * @date Created in 2020/6/29 15:21
 */
@Data
public class PageListApolloCommitHistoryResponseDTO {
    private String typeName;
    private String key;
    private String oldValue;
    private String newValue;
    //private String comment;
    private String commitTime;
}
