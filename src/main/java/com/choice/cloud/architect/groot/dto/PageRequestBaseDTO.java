package com.choice.cloud.architect.groot.dto;

import lombok.Data;

/**
 * <p>
 * 请求分页基础DTO
 * </p>
 *
 * @author LZ
 * @date Created in 2020/8/10 11:22
 */
@Data
public class PageRequestBaseDTO {
    /**
     * 页码
     */
    private Integer pageNum = 1;
    /**
     * 每页显示条数
     */
    private Integer pageSize = 10;
}
