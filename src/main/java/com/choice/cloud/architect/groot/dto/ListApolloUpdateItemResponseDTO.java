package com.choice.cloud.architect.groot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 已修改的Apollo配置项集合响应体
 * </p>
 *
 * @author LZ
 * @date Created in 2020/6/15 20:35
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ListApolloUpdateItemResponseDTO {
    /**
     * key
     */
    private String changeItemKey;
    /**
     * 发布的值
     */
    private String oldValue;
    /**
     * 未发布的值
     */
    private String changeItemValue;
    /**
     * 发布类型
     */
    private Integer changeType;
    ///**
    // * 修改人
    // */
    //private String changeItemKey;
    ///**
    // * 修改时间
    // */
    //private String changeItemKey;
}
