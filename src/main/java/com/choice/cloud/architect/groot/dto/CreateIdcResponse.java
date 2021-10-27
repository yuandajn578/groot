package com.choice.cloud.architect.groot.dto;

import java.util.List;

import lombok.Data;

/**
 * @ClassName CreateIdcResponse
 * @Description 创建idc响应体
 * @Author LZ
 * @Date 2020/4/10 14:22
 * @Version 1.0
 */
@Data
public class CreateIdcResponse {
    /**
     * 不存在的appId
     */
    private List<String> errorAppIdList;
    /**
     * 描述
     */
    private String desc;
}
