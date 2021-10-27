package com.choice.cloud.architect.groot.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @classDesc:
 * @author:yuxiaopeng
 * @createTime: 2019-02-26
 * @version: v1.0.0
 * @email: yxpchoicesoft.com.cn
 */
@Mapper
public interface MockMapper {
    Integer selectNametById(String id);
}
