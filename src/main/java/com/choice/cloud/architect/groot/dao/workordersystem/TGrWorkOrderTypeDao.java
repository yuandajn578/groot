package com.choice.cloud.architect.groot.dao.workordersystem;

import java.util.List;

import com.choice.cloud.architect.groot.model.workordersystem.TGrWorkOrderType;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 工单类型表(TGrWorkOrderType)表数据库访问层
 *
 * @author LZ
 * @since 2020-08-10 11:02:17
 */
@Repository
public interface TGrWorkOrderTypeDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    TGrWorkOrderType queryById(Long id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<TGrWorkOrderType> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 通过实体作为筛选条件查询
     *
     * @param tGrWorkOrderType 实例对象
     * @return 对象列表
     */
    List<TGrWorkOrderType> queryAll(TGrWorkOrderType tGrWorkOrderType);

    /**
     * 新增数据
     *
     * @param tGrWorkOrderType 实例对象
     * @return 影响行数
     */
    int insert(TGrWorkOrderType tGrWorkOrderType);

    /**
     * 修改数据
     *
     * @param tGrWorkOrderType 实例对象
     * @return 影响行数
     */
    int update(TGrWorkOrderType tGrWorkOrderType);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Long id);

}