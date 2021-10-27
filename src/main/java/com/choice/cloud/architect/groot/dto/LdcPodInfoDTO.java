package com.choice.cloud.architect.groot.dto;

import lombok.Data;

import java.util.List;

/**
 * @author zhangkun
 */
@Data
public class LdcPodInfoDTO {
    private String id;
    private String idc;
    private String ldc;
    private Integer podNum;
    private List<ListPodInfoDTO> podList;
}
