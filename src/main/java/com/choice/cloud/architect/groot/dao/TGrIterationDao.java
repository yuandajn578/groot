package com.choice.cloud.architect.groot.dao;

import java.util.List;

import com.choice.cloud.architect.groot.dto.iteration.PageListIterActionRequestDTO;
import com.choice.cloud.architect.groot.model.TGrIteration;
import org.apache.ibatis.annotations.Param;

/**
 * 迭代表(TGrIteration)表数据库访问层
 *
 * @author makejava
 * @since 2020-08-19 16:28:49
 */
public interface TGrIterationDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    TGrIteration queryById(Long id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<TGrIteration> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 通过实体作为筛选条件查询
     *
     * @param tGrIteration 实例对象
     * @return 对象列表
     */
    List<TGrIteration> queryAll(TGrIteration tGrIteration);

    /**
     * 新增数据
     *
     * @param tGrIteration 实例对象
     * @return 影响行数
     */
    int insert(TGrIteration tGrIteration);

    int insertDynamic(TGrIteration tGrIteration);

    /**
     * 修改数据
     *
     * @param tGrIteration 实例对象
     * @return 影响行数
     */
    int update(TGrIteration tGrIteration);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Long id);

    /**
     * 分页查询迭代
     * @param request
     * @return
     */
    List<TGrIteration> queryAllByPage(PageListIterActionRequestDTO request);

    TGrIteration queryByOid(@Param("oid") String oid);
}