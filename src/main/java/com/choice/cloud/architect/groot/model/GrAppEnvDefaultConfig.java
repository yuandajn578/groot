package com.choice.cloud.architect.groot.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/9 13:42
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class GrAppEnvDefaultConfig extends BaseBizModel {

    private String appCode;

    /**
     * 环境类型
     */
    private String envType;

    /**
     * 环境分组
     */
    private String envCode;

    /**
     * healthcheck_port 健康检查端口
     */
    private int healthCheckPort = 8080;

    /**
     * healthcheck_path	string	是	/ping	监控检查地址	/ping
     */
    private String healthCheckPath = "/ping";

    /**
     * healthcheck_initialDelaySeconds	int	是	120	监控检查初始化时间，单位：秒	180
     */
    private int healthCheckInitialDelaySeconds = 90;

    /**
     * healthcheck_periodSeconds	int	是	3	监控检查间隔，单位：秒	3
     */
    private int healthCheckPeriodSeconds = 6;

    /**
     * healthcheck_successThreshold	int	是	1	允许最大失败次数	1
     */
    private int healthCheckSuccessThreshold = 1;

    /**
     * 从上次检查成功后认定检查失败的检查次数阈值（必须是连续失败），默认为 1
     */
    private int healthCheckFailureThreshold = 1;

    /**
     * healthcheck_timeoutSeconds	int	是	2	检查检查超时时间，单位：秒	2
     */
    private int healthCheckTimeoutSeconds = 2;

    /**
     * sonar	int	是	1	是否开启sonar代码质量检测	0或者1
     */
    private int sonar = 1;

    /**
     * sonar_path	string	否	./	sonar扫描的文件路径	./ch
     */
    private String sonarPath = "./";

    /**
     * 批量大小
     */
    private int batchSize;

    /**
     * 部署策略
     */
    private String deployStrategy;

    /**
     * 是否开启MQ分组
     */
    private String mqGraySwitch;

    /**
     * jvm 配置參數
     */
    private String javaOptions;

    /**
     * 是否开启发送消息的MQ灰度分组
     */
    private String mqGrayProducerSwitch;

    /**
     * 是否开启发送消息的MQ灰度分组
     */
    private String mqGrayConsumerSwitch;
}
