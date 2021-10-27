package com.choice.cloud.architect.groot.remote.milkyway;

import com.choice.cloud.architect.groot.option.GlobalConst;
import lombok.Data;

/**
 * <p>
 * 根据应用编码查询应用配置信息响应体
 * </p>
 *
 * @author LZ
 * @date Created in 2020/8/14 13:52
 */
@Data
public class AppConfigInfoResponseDTO {
    /**
     * 业务主键
     */
    private String oid;
    /**
     * 应用编码
     */
    private String appCode;
    /**
     * 批量大小
     */
    private String batchSize = "1";
    /**
     * JVM参数
     */
    private String javaOptions = GlobalConst.JVM_OPT;
    /**
     * 健康检查地址
     */
    private String healthCheckPath = "/ping";
    /**
     * 健康检查端口号
     */
    private Integer healthCheckPort = 8080;
    /**
     * 健康检查间隔
     */
    private String healthCheckPeriodSeconds = "6";
    /**
     * 健康检查初始化检查时间
     */
    private String healthCheckInitialDelaySeconds = "90";
    /**
     * 健康检查超时时间
     */
    private String healthCheckTimeoutSeconds = "2";
    /**
     * 健康检查连续成功阈值
     */
    private String healthCheckSuccessThreshold = "1";
    /**
     * 健康检查不连续失败阈值
     */
    private String healthCheckFailureThreshold = "1";
    /**
     * 是否开启sonar（0未开启 1开启）
     */
    private Integer sonar = 1;
    /**
     * sonar扫描路径
     */
    private String sonarPath = "./";
    /**
     * jar 打包地址(即Dockerfile路径)
     */
    private String jarFilePath = "./";
}
