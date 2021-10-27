package com.choice.cloud.architect.groot.remote;

import lombok.Data;

import java.util.Set;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/26 17:33
 */

@Data
public class ApplicationListByAppCodeParam {

    private Set<String> appCodeSet;
}
