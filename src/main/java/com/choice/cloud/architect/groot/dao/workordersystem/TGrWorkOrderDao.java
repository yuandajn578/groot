package com.choice.cloud.architect.groot.dao.workordersystem;

import java.util.List;

import com.choice.cloud.architect.groot.dto.workordersystem.PageListWorkOrderRequestDTO;
import com.choice.cloud.architect.groot.model.workordersystem.TGrWorkOrder;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 工单表(TGrWorkOrder)表数据库访问层
 *
 * @author LZ
 * @since 2020-08-10 11:02:13
 */
@Repository
public interface TGrWorkOrderDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    TGrWorkOrder queryById(Long id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<TGrWorkOrder> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 通过实体作为筛选条件查询
     *
     * @param tGrWorkOrder 实例对象
     * @return 对象列表
     */
    List<TGrWorkOrder> queryAll(TGrWorkOrder tGrWorkOrder);

    /**
     * 新增数据
     *
     * @param tGrWorkOrder 实例对象
     * @return 影响行数
     */
    int insert(TGrWorkOrder tGrWorkOrder);

    /**
     * 修改数据
     *
     * @param tGrWorkOrder 实例对象
     * @return 影响行数
     */
    int update(TGrWorkOrder tGrWorkOrder);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Long id);

    /**
     * 分页查询工单
     *
     * @param requestDTO
     * @return
     */
    List<TGrWorkOrder> queryAllByPage(PageListWorkOrderRequestDTO requestDTO);

    /**
     * 根据工单编码查询工单信息
     *
     * @param orderCode
     * @return
     */
    TGrWorkOrder queryByOrderCode(@Param("orderCode") String orderCode);

}