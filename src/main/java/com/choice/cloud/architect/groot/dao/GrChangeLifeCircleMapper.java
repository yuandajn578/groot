package com.choice.cloud.architect.groot.dao;

import com.choice.cloud.architect.groot.model.GrChangeLifeCircle;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/13 18:55
 */
@Mapper
@Repository
public interface GrChangeLifeCircleMapper {

    int insert(GrChangeLifeCircle grChangeLifeCircle);

    List<GrChangeLifeCircle> listWithPage(String changeId, String envType, Integer deleteFlag);

    List<GrChangeLifeCircle> list(String changeId, String envType, Integer limit, Integer deleteFlag);
}
