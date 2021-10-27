package com.choice.cloud.architect.groot.eureka.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @ClassName ServiceInstanceDTO
 * @Description TODO
 * @Author Guangshan Wang
 * @Date 2020/4/26/026 18:42
 */
@Data
public class ServiceGroupInstanceDTO implements Serializable {
    private String serviceId;
    private String serviceGroup;
    private List<InstanceDTO> instanceList;
}
