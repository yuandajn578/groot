package com.choice.cloud.architect.groot.dto;

import lombok.Data;

/**
 * @author zhangkun
 */
@Data
public class ListPublishDetailDTO {
    private String publishId;
    private String idc;
    private String ldc;
    private String podName;
}
