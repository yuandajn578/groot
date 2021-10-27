package com.choice.cloud.architect.groot.dto.project;

import com.choice.cloud.architect.groot.dto.PageRequestBaseDTO;
import lombok.Data;

/**
 * <p>
 * 分页查询项目请求体
 * </p>
 *
 * @author LZ
 * @date Created in 2020/8/19 16:50
 */
@Data
public class PageListProjectRequestDTO extends PageRequestBaseDTO {
    /**
     * 项目名称
     */
    private String projectName;
}
