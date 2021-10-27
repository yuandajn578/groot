package com.choice.cloud.architect.groot.process.dto;

import lombok.Data;

/**
 * @ClassName ProcessInfoQueryDTO
 * @Description 查看审批详情的请求体
 * @Author Guangshan Wang
 * @Date 2020/3/9/009 13:29
 */
@Data
public class ProcessInfoQueryDTO {
    /**
     * 审批id
     */
    String instanceId;
}
