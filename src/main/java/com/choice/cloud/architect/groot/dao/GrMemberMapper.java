package com.choice.cloud.architect.groot.dao;

import com.choice.cloud.architect.groot.model.GrMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * (GrMember)表数据库访问层
 *
 * @author makejava
 * @since 2020-03-03 16:55:18
 */
@Mapper
@Repository
public interface GrMemberMapper {


    /**
     * 变更ID查询
     * @param changeIdList
     * @return
     */
    List<GrMember> queryByChangeIds(@Param("memberType") String memberType,
                                    @Param("changeIdList") List<String> changeIdList, @Param("deleteFlag") int deleteFlag);
    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    GrMember queryById(Object id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<GrMember> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param grMember 实例对象
     * @return 对象列表
     */
    List<GrMember> queryAll(GrMember grMember);

    /**
     * 新增数据
     *
     * @param grMember 实例对象
     * @return 影响行数
     */
    int insert(GrMember grMember);

    /**
     * 批量新增
     * @param list
     * @return
     */
    int batchInsert(List<GrMember> list);
    /**
     * 修改数据
     *
     * @param grMember 实例对象
     * @return 影响行数
     */
    int update(GrMember grMember);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Object id);

    int deleteByChangeIdAndMemberType(@Param("changeId") String changeId, @Param("type") String type, @Param("deleteFlag") int deleteFlag);

    List<GrMember> listByChangeIdAndType(@Param("changeId") String changeId, @Param("type") String type, @Param("deleteFlag") int deleteFlag);

    List<GrMember> listByMemberIdAndType(@Param("memberId") String memberId, @Param("memberType") String memberType, @Param("deleteFlag") int deleteFlag);

    List<GrMember> listByMemberId(@Param("memberId") String memberId, @Param("deleteFlag") int deleteFlag);

    List<GrMember> queryByChangeId(@Param("changeId") String changeId);
}
