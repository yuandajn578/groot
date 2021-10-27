package com.choice.cloud.architect.groot.dataobject;

import lombok.Data;
import lombok.ToString;

/**
 * @author zhangkun
 */
@Data
@ToString
public class EnvQueryPageDO {
    private String envName;

    private String envType;
}
