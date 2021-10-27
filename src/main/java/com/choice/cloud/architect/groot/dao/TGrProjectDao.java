package com.choice.cloud.architect.groot.dao;

import java.util.List;

import com.choice.cloud.architect.groot.dto.project.PageListProjectRequestDTO;
import com.choice.cloud.architect.groot.model.TGrProject;
import org.apache.ibatis.annotations.Param;

/**
 * 项目表(TGrProject)表数据库访问层
 *
 * @author makejava
 * @since 2020-08-19 16:28:54
 */
public interface TGrProjectDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    TGrProject queryById(Long id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<TGrProject> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 通过实体作为筛选条件查询
     *
     * @param tGrProject 实例对象
     * @return 对象列表
     */
    List<TGrProject> queryAll(TGrProject tGrProject);

    /**
     * 新增数据
     *
     * @param tGrProject 实例对象
     * @return 影响行数
     */
    int insert(TGrProject tGrProject);

    /**
     * 修改数据
     *
     * @param tGrProject 实例对象
     * @return 影响行数
     */
    int update(TGrProject tGrProject);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Long id);

    List<TGrProject> queryAllByPage(PageListProjectRequestDTO request);

    TGrProject queryByOid(@Param("oid") String oid);
}