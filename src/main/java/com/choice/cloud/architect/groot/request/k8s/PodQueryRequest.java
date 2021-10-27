package com.choice.cloud.architect.groot.request.k8s;

import com.choice.cloud.architect.groot.request.PageRequest;
import lombok.Data;


/**
 * @author lanboo
 * @date 2020/6/2 14:58
 * @desc
 */
@Data
public class PodQueryRequest extends PageRequest {
    private String env;
}
