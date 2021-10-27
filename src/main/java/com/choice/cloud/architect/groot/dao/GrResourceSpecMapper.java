package com.choice.cloud.architect.groot.dao;

import com.choice.cloud.architect.groot.model.GrResourceSpec;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zhangkun
 */
@Mapper
@Repository
public interface GrResourceSpecMapper {
    int delete(Integer id);

    int insert(GrResourceSpec grResourceSpec);

    int insertDynamic(GrResourceSpec grResourceSpec);

    int updateDynamic(GrResourceSpec grResourceSpec);

    int update(GrResourceSpec grResourceSpec);

    GrResourceSpec selectById(Integer id);

    List<GrResourceSpec> list();
}
