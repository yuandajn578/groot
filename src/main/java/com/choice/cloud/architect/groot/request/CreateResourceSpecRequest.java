package com.choice.cloud.architect.groot.request;

import lombok.Data;
import lombok.ToString;

/**
 * @author zhangkun
 */
@Data
@ToString
public class CreateResourceSpecRequest {
    /**
     * 计算资源类型名称
     */
    private String name;

    /**
     * cpu(core)
     */
    private String cpu;

    /**
     * memory(g)
     */
    private String memory;

    /**
     * 排序
     */
    private Integer displayOrder;
}
