package com.choice.cloud.architect.groot.dataobject;

import com.choice.cloud.architect.groot.model.GrPublish;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @Author: zhangguoquan
 * @Date: 2020/4/7 14:16
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class PublishQueryDO extends GrPublish {

    private String searchValue;
}
