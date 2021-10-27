package com.choice.cloud.architect.groot.request;

import com.choice.cloud.architect.groot.option.GlobalConst;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

import static com.choice.cloud.architect.groot.enums.DeployStrategyEnum.START_FIRST;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/5 14:20
 */
@Data
@ApiModel(description = "发布构建参数")
public class PublishBuildRequest {

    @ApiModelProperty(value = "发布单oid")
    @NotBlank(message = "oid不能为空")
    private String oid;

    @ApiModelProperty(value = "编辑发布true，直接发布false，默认false")
    private Boolean editFlag = false;

    /**
     *  healthcheck_port 健康检查端口
     */
    @ApiModelProperty(value = "健康检查端口")
    private int healthCheckPort = 8080;

    /**
     *  healthcheck_path	string	是	/ping	监控检查地址	/ping
     */
    @ApiModelProperty(value = "监控检查地址")
    private String healthCheckPath = "/ping";

    /**
     * healthcheck_initialDelaySeconds	int	是	120	监控检查初始化时间，单位：秒	180
     */
    @ApiModelProperty(value = "监控检查初始化时间")
    private int healthCheckInitialDelaySeconds = 90;

    /**
     * healthcheck_periodSeconds	int	是	3	监控检查间隔，单位：秒	3
     */
    @ApiModelProperty(value = "监控检查间隔，单位：秒")
    private int healthCheckPeriodSeconds = 6;

    /**
     * healthcheck_successThreshold	int	是	1	允许最大失败次数	1
     */
    @ApiModelProperty(value = "允许最大失败次数")
    private int healthCheckSuccessThreshold = 1;

    /**
     * 从上次检查成功后认定检查失败的检查次数阈值（必须是连续失败），默认为1
     */
    @ApiModelProperty(value = "从上次检查成功后认定检查失败的检查次数阈值")
    private int healthCheckFailureThreshold = 1;

    /**
     * healthcheck_timeoutSeconds	int	是	2	检查检查超时时间，单位：秒	2
     */
    @ApiModelProperty(value = "检查检查超时时间，单位：秒")
    private int healthCheckTimeoutSeconds = 2;

    /**
     * sonar	int	是	1	是否开启sonar代码质量检测	0或者1
     */
    @ApiModelProperty(value = "是否开启sonar代码质量检测")
    private int sonar = 1;

    /**
     * sonar_path	string	否	./	sonar扫描的文件路径	./ch
     */
    @ApiModelProperty(value = "sonar扫描的文件路径")
    private String sonarPath = "./";

    /**
     * 批量大小
     */
    @ApiModelProperty(value = "批量大小")
    private int batchSize = 1;

    /**
     * 部署策略
     */
    @ApiModelProperty(value = "发布策略:startFirst、stopFirst、deleteAllFirst")
    private String deployStrategy = START_FIRST.getValue();

    @ApiModelProperty(value = "是否开启MQ分组（on  off）")
    private String mqGraySwitch = "off";

    @ApiModelProperty(value = "jvm 参数")
    private String javaOptions = GlobalConst.JVM_OPT;

    /**
     * 是否开启发送消息的MQ灰度开关
     * MQ.PRODUCER.GRAY.SWITCH
     */
    private String mqGrayProducerSwitch = "off";
    /**
     * 是否开启接收消息的MQ灰度开关
     * MQ.CONSUMER.GRAY.SWITCH
     */
    private String mqGrayConsumerSwitch = "off";

    /**
     * pod编排信息，json格式
     */
    private String podOrchestration;
}
