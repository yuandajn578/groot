package com.choice.cloud.architect.groot.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author zhangkun
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class GrPublishDetail extends BaseBizModel{
    /**
     * id
     */
    private Integer id;

    /**
     * oid
     */
    private String oid;

    /**
     * 发布单id
     */
    private String publishId;

    /**
     * idc
     */
    private String idc;

    /**
     * ldc
     */
    private String ldc;

    /**
     * pod名称
     */
    private String podName;
}
