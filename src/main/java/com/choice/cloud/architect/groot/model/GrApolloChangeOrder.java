package com.choice.cloud.architect.groot.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * <p>
 * Apollo应用变更单Model
 * </p>
 *
 * @author zhangkun
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class GrApolloChangeOrder extends BaseBizModel {

    /**
     * 变更单编码
     */
    private String changeId;

    /** apollo应用ID */
    private String appId;

    /** 环境 */
    private String env;

    /** 集群 */
    private String cluster;

    /** 命名空间 */
    private String namespace;

    /**
     * apollo变更子项key值
     */
    private String changeItemKey;

    /**
     * apollo变更子项value值
     */
    private String changeItemValue;

    /**
     * 描述信息
     */
    private String description;

}
