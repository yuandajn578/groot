package com.choice.cloud.architect.groot.dto.iteration;

import com.choice.cloud.architect.groot.dto.PageRequestBaseDTO;
import lombok.Data;

/**
 * <p>
 * 分页查询迭代请求体
 * </p>
 *
 * @author LZ
 * @date Created in 2020/8/19 16:50
 */
@Data
public class PageListIterActionRequestDTO extends PageRequestBaseDTO {
    /**
     * 迭代名称
     */
    private String iterationName;
    /**
     * 迭代状态
     */
    private String iterationStatus;
}
