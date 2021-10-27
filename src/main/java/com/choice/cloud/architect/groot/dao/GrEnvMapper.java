package com.choice.cloud.architect.groot.dao;

import com.choice.cloud.architect.groot.dataobject.EnvQueryPageDO;
import com.choice.cloud.architect.groot.model.GrEnv;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * gr_env表数据库操作Mapper
 * </p>
 *
 * @author zhangkun
 */
@Mapper
@Repository
public interface GrEnvMapper {
    int delete(@Param("oid") String oid, @Param("updateUser") String updateUser);

    int insert(GrEnv grEnv);

    int insertDynamic(GrEnv grEnv);

    int updateDynamic(GrEnv grEnv);

    int update(GrEnv grEnv);

    GrEnv selectById(@Param("oid") String oid);

    List<GrEnv> listWithPage(@Param("envQueryPageDO") EnvQueryPageDO envQueryPageDO);

    GrEnv selectByCode(@Param("envType") String envType, @Param("envCode") String envCode);

    List<GrEnv> selectByType(@Param("envType") String envType);

    int updateStatus(@Param("id") String id, @Param("oldStatus") Integer oldStatus, @Param("expectStatus") Integer expectStatus, @Param("updateUser") String updateUser);
}
