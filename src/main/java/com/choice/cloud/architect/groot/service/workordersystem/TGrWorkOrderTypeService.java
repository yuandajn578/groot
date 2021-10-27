package com.choice.cloud.architect.groot.service.workordersystem;

import java.util.List;

import com.choice.cloud.architect.groot.dto.workordersystem.CreateWorkOrderTypeRequestDTO;
import com.choice.cloud.architect.groot.dto.workordersystem.ListWorkOrderTypeResponseDTO;
import com.choice.cloud.architect.groot.model.workordersystem.TGrWorkOrderType;

/**
 * 工单类型表(TGrWorkOrderType)表服务接口
 *
 * @author LZ
 * @since 2020-08-10 11:02:18
 */
public interface TGrWorkOrderTypeService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    TGrWorkOrderType queryById(Long id);

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<TGrWorkOrderType> queryAllByLimit(int offset, int limit);

    /**
     * 新增数据
     *
     * @param tGrWorkOrderType 实例对象
     * @return 实例对象
     */
    TGrWorkOrderType insert(TGrWorkOrderType tGrWorkOrderType);

    /**
     * 修改数据
     *
     * @param tGrWorkOrderType 实例对象
     * @return 实例对象
     */
    TGrWorkOrderType update(TGrWorkOrderType tGrWorkOrderType);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Long id);

    /**
     * 查询所有的工单类型
     * @return
     */
    List<ListWorkOrderTypeResponseDTO> list();

    /**
     * 新增工单类型
     * @param request
     * @return
     */
    TGrWorkOrderType create(CreateWorkOrderTypeRequestDTO request);
}