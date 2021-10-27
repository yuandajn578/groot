package com.choice.cloud.architect.groot.dao;

import com.choice.cloud.architect.groot.model.GrChangeRecode;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * (GrChangeRecode)表数据库访问层
 *
 * @author makejava
 * @since 2020-03-03 16:55:20
 */
public interface GrChangeRecodeMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    GrChangeRecode queryById(Object id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<GrChangeRecode> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param grChangeRecode 实例对象
     * @return 对象列表
     */
    List<GrChangeRecode> queryAll(GrChangeRecode grChangeRecode);

    /**
     * 新增数据
     *
     * @param grChangeRecode 实例对象
     * @return 影响行数
     */
    int insert(GrChangeRecode grChangeRecode);

    /**
     * 修改数据
     *
     * @param grChangeRecode 实例对象
     * @return 影响行数
     */
    int update(GrChangeRecode grChangeRecode);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Object id);

    List<GrChangeRecode>  listWithPage(GrChangeRecode grChangeRecode);

}