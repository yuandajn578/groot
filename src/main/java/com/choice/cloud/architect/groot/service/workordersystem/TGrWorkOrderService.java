package com.choice.cloud.architect.groot.service.workordersystem;

import java.util.List;

import com.choice.cloud.architect.groot.dto.workordersystem.ActiveCloseWorkOrderRequestDTO;
import com.choice.cloud.architect.groot.dto.workordersystem.ConfirmWorkOrderRequestDTO;
import com.choice.cloud.architect.groot.dto.workordersystem.CreateWorkOrderRequestDTO;
import com.choice.cloud.architect.groot.dto.workordersystem.HandleFinishWorkOrderRequestDTO;
import com.choice.cloud.architect.groot.dto.workordersystem.HandleRejectWorkOrderRequestDTO;
import com.choice.cloud.architect.groot.dto.workordersystem.PageListWorkOrderRequestDTO;
import com.choice.cloud.architect.groot.dto.workordersystem.PageListWorkOrderResponseDTO;
import com.choice.cloud.architect.groot.dto.workordersystem.StartHandleWorkOrderRequestDTO;
import com.choice.cloud.architect.groot.dto.workordersystem.WorkOrderInfoByCodeResponseDTO;
import com.choice.cloud.architect.groot.model.workordersystem.TGrWorkOrder;
import com.choice.cloud.architect.groot.option.WebPage;
import com.choice.driver.jwt.entity.AuthUser;

/**
 * 工单表(TGrWorkOrder)表服务接口
 *
 * @author makejava
 * @since 2020-08-10 11:02:14
 */
public interface TGrWorkOrderService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    TGrWorkOrder queryById(Long id);

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<TGrWorkOrder> queryAllByLimit(int offset, int limit);

    /**
     * 新增数据
     *
     * @param tGrWorkOrder 实例对象
     * @return 实例对象
     */
    TGrWorkOrder insert(TGrWorkOrder tGrWorkOrder);

    /**
     * 修改数据
     *
     * @param tGrWorkOrder 实例对象
     * @return 实例对象
     */
    TGrWorkOrder update(TGrWorkOrder tGrWorkOrder);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Long id);

    /**
     * 分页查询工单列表
     * @param request
     * @return
     */
    WebPage<List<PageListWorkOrderResponseDTO>> pageList(PageListWorkOrderRequestDTO request, AuthUser authUser);

    /**
     * 处理完成工单
     * @param request
     * @return
     */
    boolean handleFinish(HandleFinishWorkOrderRequestDTO request, AuthUser authUser);

    /**
     * 处理拒绝工单
     * @param request
     * @return
     */
    boolean handleReject(HandleRejectWorkOrderRequestDTO request, AuthUser authUser);

    /**
     * 确认工单处理完成
     * @param request
     */
    boolean confirm(ConfirmWorkOrderRequestDTO request, AuthUser authUser);

    /**
     * 主动关闭工单
     * @param request
     */
    boolean activeClose(ActiveCloseWorkOrderRequestDTO request, AuthUser authUser);

    /**
     * 新增工单
     * @param request
     * @return
     */
    TGrWorkOrder create(CreateWorkOrderRequestDTO request, AuthUser authUser);

    /**
     * 根据工单编码查看工单信息
     * @param orderCode
     * @return
     */
    WorkOrderInfoByCodeResponseDTO queryByCode(String orderCode);

    /**
     * 接单
     * @param request
     * @param authUser
     * @return
     */
    boolean startHandle(StartHandleWorkOrderRequestDTO request, AuthUser authUser);
}