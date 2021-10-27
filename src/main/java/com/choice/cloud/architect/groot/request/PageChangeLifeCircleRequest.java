package com.choice.cloud.architect.groot.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/14 10:29
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class PageChangeLifeCircleRequest extends PageRequest{

    private String changeId;
    private String envType;
}
