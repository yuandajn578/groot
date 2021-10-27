package com.choice.cloud.architect.groot.eureka.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @ClassName InstanceDTO
 * @Description eureka 服务实例信息
 * @Author Guangshan Wang
 * @Date 2020/4/26/026 16:04
 */
@Data
public class InstanceDTO implements Serializable {
    private String instanceId;
    private Integer port;
    private String host;
    private String serviceId;
    private String uri;
    private String serverGroup;
    private LocalDateTime registrationTimestamp;
    private LocalDateTime renewalTimestamp;
    private LocalDateTime serviceUpTimestamp;
    private String status;
}
