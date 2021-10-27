package com.choice.cloud.architect.groot.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@ApiModel(description = "应用变更单参数")
@Getter
@Setter
public class UpdateChangeRequest {

    @ApiModelProperty(value = "变更单ID",required = true)
    @NotEmpty(message = "变更单ID不能为空")
    private String oid;

    /** 计划发布时间 */
    @ApiModelProperty(value = "计划发布时间",required = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;

    /** 发布后是否删除分支 */
    @ApiModelProperty(value = "发布后是否删除分支",required = true)
    private Integer  deleteBranch;


}
