package com.choice.cloud.architect.groot.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author zhangkun
 */
@Data
@ToString
@ApiModel(value = "发布审核单查询列表Model")
public class ListPublishAuditRequest extends PageRequest {
    @ApiModelProperty(value = "状态 (to_be_committed:待提交,pending_approval:审批中,canceled:已取消,approved:审批通过,rejected:审批拒绝）")
    private String status;

    @ApiModelProperty(value = "查询参数")
    private String searchValue;
}
