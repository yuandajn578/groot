package com.choice.cloud.architect.groot.dto;

import com.choice.cloud.architect.groot.model.GrAppEnvRel;
import lombok.Data;

import java.util.List;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/27 16:39
 */
@Data
public class ChangeAppEnvDTO {

    private String changeName;

    private ListChangeDTO changeInfo;

    private List<GrAppEnvRel> appEnvList;
}
