package com.choice.cloud.architect.groot.dto.gitlab;

import java.util.List;

import lombok.Data;

/**
 * <p>
 * 获取所有GitLab的group分组信息响应体
 * </p>
 *
 * @author LZ
 * @date Created in 2020/7/22 14:16
 */
@Data
public class GetAllGroupResponseDTO {
    private List<String> groupNameList;
}
