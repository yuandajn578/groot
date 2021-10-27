package com.choice.cloud.architect.groot.remote.milkyway;

import lombok.Data;

/**
 * <p>
 * 根据应用code查询架构域信息请求体
 * </p>
 *
 * @author LZ
 * @date Created in 2020/7/24 17:31
 */
@Data
public class GetInfoByAppCodeRequestDTO {
    private String appCode;
}
