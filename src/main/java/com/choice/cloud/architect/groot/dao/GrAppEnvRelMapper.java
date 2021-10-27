package com.choice.cloud.architect.groot.dao;

import com.choice.cloud.architect.groot.model.GrAppEnvRel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zhangkun
 */
@Mapper
@Repository
public interface GrAppEnvRelMapper {
    int delete(Integer id);

    int insert(GrAppEnvRel grAppEnvRel);

    int batchInsert(List<GrAppEnvRel> appEnvRelList);

    int insertDynamic(GrAppEnvRel grAppEnvRel);

    int updateDynamic(GrAppEnvRel grAppEnvRel);

    int update(GrAppEnvRel grAppEnvRel);

    GrAppEnvRel selectById(Integer id);

    GrAppEnvRel selectByAppCodeAndEnvCode(@Param("changeId") String changeId, @Param("appCode") String appCode, @Param("envType") String envType, @Param("envCode") String envCode);

    List<GrAppEnvRel> selectByAppCode(@Param("appCodeList") List<String> appCodeList);

    List<GrAppEnvRel> selectByEnv(@Param("envType") String envType, @Param("envCode") String envCode);

    List<GrAppEnvRel> listByAppCodeAndEnvCode(@Param("changeId") String changeId, @Param("appCode") String appCode, @Param("envType") String envType, @Param("envCode") String envCode);

    List<GrAppEnvRel> listByChangeId(@Param("changeId") String changeId);

    int deleteByChangeIdAndEnv(@Param("changeId") String changeId, @Param("envType") String envType, @Param("envCode") String envCode);

    List<GrAppEnvRel> selectByEnvType(@Param("envType") String envType);
}
