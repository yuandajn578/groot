package com.choice.cloud.architect.groot.response.k8s;

import lombok.Data;

import java.util.Date;

/**
 * @author lanboo
 * @date 2020/6/2 15:49
 * @desc
 */
@Data
public class PodResponse {
    /**
     * pod name
     */
    private String name;
    /**
     * 创建时间
     */
    private String creatTime;
    /**
     * namespace
     */
    private String nameSpace;
    /**
     *
     */
    private String kind;
}
