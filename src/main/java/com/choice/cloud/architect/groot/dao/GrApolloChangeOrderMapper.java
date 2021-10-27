package com.choice.cloud.architect.groot.dao;

import com.choice.cloud.architect.groot.model.GrApolloChangeOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangkun
 */
public interface GrApolloChangeOrderMapper {
    int delete(Integer id);

    int insert(GrApolloChangeOrder grApolloChangeOrder);

    int batchInsert(@Param("list") List<GrApolloChangeOrder> list);

    int insertDynamic(GrApolloChangeOrder grApolloChangeOrder);

    int updateDynamic(GrApolloChangeOrder grApolloChangeOrder);

    int update(GrApolloChangeOrder grApolloChangeOrder);

    GrApolloChangeOrder selectById(@Param("oid") String oid);

    List<GrApolloChangeOrder> listWithPage(GrApolloChangeOrder grApolloChangeOrder);

    int publish(GrApolloChangeOrder grApolloChangeOrder);

    int updateDeleteFlag(GrApolloChangeOrder grApolloChangeOrder);
}
