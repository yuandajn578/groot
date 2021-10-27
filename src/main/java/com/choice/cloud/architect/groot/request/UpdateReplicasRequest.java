package com.choice.cloud.architect.groot.request;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @Author: zhangguoquan
 * @Date: 2020/5/19 10:59
 */
@Data
@ApiModel(description = "扩缩容参数")
public class UpdateReplicasRequest {

    private String envType;

    private String envCode;

    private String appCode;

    private Integer replicas;

}
