package com.choice.cloud.architect.groot.dao;

import com.choice.cloud.architect.groot.model.GrAppEnvDefaultConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/9 13:41
 */
@Mapper
@Repository
public interface GrAppEnvDefaultConfigMapper {

    int insert(GrAppEnvDefaultConfig config);

    GrAppEnvDefaultConfig getByAppAndEnv(@Param("appCode") String appCode, @Param("envType") String envType,
                                         @Param("envCode") String envCode, @Param("deleteFlag") int deleteFlag);

    int update(GrAppEnvDefaultConfig config);
}
