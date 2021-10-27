package com.choice.cloud.architect.groot.request;

import com.choice.cloud.architect.groot.dto.PublishPodDetailDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/19 19:28
 */
@Data
@ApiModel(description = "发布流程参数")
public class PublishRequest {

    @NotBlank(message = "appCode不能为空")
    private String appCode;

    @NotBlank(message = "changeId不能为空")
    private String changeId;

    @NotBlank(message = "envType不能为空")
    private String envType;

    @NotBlank(message = "envCode不能为空")
    private String envCode;

    @ApiModelProperty("是否是第一次发布")
    private String firstPublish;

    @ApiModelProperty("发布策略")
    private String publishStrategy;

    private List<PublishPodDetailDTO> publishPodDetails;
}
