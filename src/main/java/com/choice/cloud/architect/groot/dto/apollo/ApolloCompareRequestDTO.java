package com.choice.cloud.architect.groot.dto.apollo;

import lombok.Data;

import java.util.List;


/**
 * @ClassName ApolloCompareRequestDTO
 * @Description 对比Apollo内容
 * @Author Guangshan Wang
 * @Date 2020/7/23/023 15:21
 */
@Data
public class ApolloCompareRequestDTO {
    private ApolloBasicDTO source;
    private ApolloBasicDTO target;
}
