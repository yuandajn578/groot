package com.choice.cloud.architect.groot.dataobject;

import lombok.Data;

/**
 * @Author: zhangguoquan
 * @Date: 2020/5/6 10:54
 */
@Data
public class LastPublishQueryDO {

    private String changeId;

    private String appCode;

    private String envType;

    private String envCode;

    private String publishStatus;

    private int deleteFlag;
}
