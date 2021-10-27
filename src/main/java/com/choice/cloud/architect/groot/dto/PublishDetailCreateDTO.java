package com.choice.cloud.architect.groot.dto;

import lombok.Data;

import java.util.List;

/**
 * @author zhangkun
 */
@Data
public class PublishDetailCreateDTO {
    private String publishId;
    private List<PublishPodDetailDTO> publishPodDetails;
}
