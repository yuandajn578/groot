package com.choice.cloud.architect.groot.process.dto;

import com.choice.cloud.architect.groot.process.enums.PlatformTypeEnum;
import lombok.Data;

/**
 * @ClassName QueryOpenIdByAccountIdDTO
 * @Description 通过accountId查询第三方账号
 * @Author Guangshan Wang
 * @Date 2020/3/10/010 19:41
 */
@Data
public class QueryOpenIdByAccountIdDTO {
    /**
     * accountId
     */
    private String userId;
    /**
     * 平台类型
     */
    private PlatformTypeEnum platformTypeEnum;
}
