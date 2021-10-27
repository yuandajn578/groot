package com.choice.cloud.architect.groot.request;

import lombok.Data;

/**
 * @author zhangkun
 */
@Data
public class ListNodeInfoRequest {
    private String idc;
    private String ldc;
    private String ip;
}
