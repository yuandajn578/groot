package com.choice.cloud.architect.groot.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author zhangkun
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class GrResourceSpec extends BaseBizModel{
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
