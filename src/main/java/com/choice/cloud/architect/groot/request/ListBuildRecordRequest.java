package com.choice.cloud.architect.groot.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/6 11:21
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class ListBuildRecordRequest extends PageRequest{

    private String publishOid;
}
