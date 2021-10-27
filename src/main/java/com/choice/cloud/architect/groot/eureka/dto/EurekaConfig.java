package com.choice.cloud.architect.groot.eureka.dto;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

/**
 * @ClassName EurekaConfig
 * @Description eureka基本配置信息
 * @Author Guangshan Wang
 * @Date 2020/3/9/009 23:23
 */
@Getter
@Repository
public class EurekaConfig {

    /**
     * 下掉服务的白名单
     */
    @Value("${eureka.shutdown.service.whiteList}")
    private String serviceWhiteList;

}
