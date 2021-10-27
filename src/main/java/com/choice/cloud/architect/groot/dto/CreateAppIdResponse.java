package com.choice.cloud.architect.groot.dto;

import lombok.Data;

/**
 * @ClassName CreateAppIdResponse
 * @Description 创建AppId响应体
 * @Author LZ
 * @Date 2020/4/12 8:30
 * @Version 1.0
 */
@Data
public class CreateAppIdResponse {
    private String name;
    private String appId;
    private String orgId;
    private String orgName;
    private String ownerName;
    private String ownerEmail;
    private int id;
    private boolean isDeleted;
    private String dataChangeCreatedBy;
    private String dataChangeCreatedTime;
    private String dataChangeLastModifiedBy;
    private String dataChangeLastModifiedTime;
}
