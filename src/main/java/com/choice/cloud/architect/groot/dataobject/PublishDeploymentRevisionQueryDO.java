package com.choice.cloud.architect.groot.dataobject;

import lombok.Data;

/**
 * @Author: zhangguoquan
 * @Date: 2020/8/5 16:28
 */
@Data
public class PublishDeploymentRevisionQueryDO {

    private String appCode;

    private String envType;

    private String envCode;

    private String publishStatus;

    private String deploymentRevision;

    private Integer deleteFlag;
}
