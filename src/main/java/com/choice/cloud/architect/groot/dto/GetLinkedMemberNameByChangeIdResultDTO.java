package com.choice.cloud.architect.groot.dto;

import java.util.List;

import lombok.Data;

/**
 * <p>
 * 根据变更单id查询关联的人员信息结果姐
 * </p>
 *
 * @author LZ
 * @date Created in 2020/8/21 13:23
 */
@Data
public class GetLinkedMemberNameByChangeIdResultDTO {
    /**
     * 开发人员名字集合
     */
    private List<String> developmentMembers;
    /**
     * 测试人员名字集合
     */
    private List<String> testMembers;
    /**
     * CR人员名字集合
     */
    private List<String> crMembers;
}
