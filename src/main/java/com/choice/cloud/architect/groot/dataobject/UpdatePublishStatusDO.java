package com.choice.cloud.architect.groot.dataobject;

import lombok.Data;

/**
 * @Author: zhangguoquan
 * @Date: 2020/6/11 13:52
 */
@Data
public class UpdatePublishStatusDO {

    private String changeId;

    private String envType;

    private String envCode;

    private String sourcePublishStatus;

    private String targetPublishStatus;
}
