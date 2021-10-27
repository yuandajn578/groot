package com.choice.cloud.architect.groot.dto;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author zhangkun
 */
@Data
@ToString
public class ListEcsInfoDTO {
    private String name;
    private String status;
    private String cpu;
    private String idc;
    private String ldc;
    private String memory;
    private String pod;
    private List<String> labels;
    private String description;
}
