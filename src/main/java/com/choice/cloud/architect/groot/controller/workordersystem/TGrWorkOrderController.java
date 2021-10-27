package com.choice.cloud.architect.groot.controller.workordersystem;

import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

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
import com.choice.cloud.architect.groot.response.Page;
import com.choice.cloud.architect.groot.response.ResponseData;
import com.choice.cloud.architect.groot.service.workordersystem.TGrWorkOrderService;
import com.choice.driver.jwt.entity.AuthUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 工单表(TGrWorkOrder)表控制层
 *
 * @author LZ
 * @since 2020-08-10 11:02:16
 */
@RestController
@RequestMapping("/workorder")
public class TGrWorkOrderController {
    /**
     * 服务对象
     */
    @Resource
    private TGrWorkOrderService tGrWorkOrderService;

    /**
     * 新增工单
     * @param request
     * @return
     */
    @PostMapping("/create")
    public ResponseData<TGrWorkOrder> create(@RequestBody @Valid CreateWorkOrderRequestDTO request, AuthUser authUser) {
        TGrWorkOrder workOrder = tGrWorkOrderService.create(request, authUser);
        return ResponseData.createBySuccess(workOrder);
    }

    /**
     * 分页查询工单列表
     * @param request
     * @return
     */
    @PostMapping("/pageList")
    public ResponseData<List<PageListWorkOrderResponseDTO>> pageList(@RequestBody @Valid PageListWorkOrderRequestDTO request, AuthUser authUser) {
        WebPage<List<PageListWorkOrderResponseDTO>> pageList = tGrWorkOrderService.pageList(request, authUser);
        Page page = new Page();
        page.setPageNum(pageList.getPageNum());
        page.setPageSize(pageList.getPageSize());
        page.setTotal(pageList.getTotal().intValue());
        return ResponseData.createBySuccess(page, pageList.getData());
    }

    /**
     * 处理完成工单
     * @param request
     * @return
     */
    @PostMapping("/handleFinish")
    public ResponseData<Boolean> handleFinish(@RequestBody @Valid HandleFinishWorkOrderRequestDTO request, AuthUser authUser) {
        boolean handleFlag = tGrWorkOrderService.handleFinish(request, authUser);
        return ResponseData.createBySuccess(handleFlag);
    }

    /**
     * 处理拒绝工单
     * @param request
     * @return
     */
    @PostMapping("/handleReject")
    public ResponseData<Boolean> handleReject(@RequestBody @Valid HandleRejectWorkOrderRequestDTO request, AuthUser authUser) {
        boolean handleFlag = tGrWorkOrderService.handleReject(request, authUser);
        return ResponseData.createBySuccess(handleFlag);
    }

    /**
     * 确认工单处理完成
     * @param request
     * @return
     */
    @PostMapping("/confirm")
    public ResponseData<Boolean> confirm(@RequestBody @Valid ConfirmWorkOrderRequestDTO request, AuthUser authUser) {
        boolean handleFlag = tGrWorkOrderService.confirm(request, authUser);
        return ResponseData.createBySuccess(handleFlag);
    }

    /**
     * 主动关闭工单
     * @param request
     * @return
     */
    @PostMapping("/activeClose")
    public ResponseData<Boolean> activeClose(@RequestBody @Valid ActiveCloseWorkOrderRequestDTO request, AuthUser authUser) {
        boolean handleFlag = tGrWorkOrderService.activeClose(request, authUser);
        return ResponseData.createBySuccess(handleFlag);
    }

    /**
     * 接单
     * @param request
     * @param authUser
     * @return
     */
    @PostMapping("/startHandle")
    public ResponseData<Boolean> startHandle(@RequestBody @Valid StartHandleWorkOrderRequestDTO request, AuthUser authUser) {
        boolean handleFlag = tGrWorkOrderService.startHandle(request, authUser);
        return ResponseData.createBySuccess(handleFlag);
    }

    /**
     * 根据工单编码查看工单信息
     * @param orderCode
     * @return
     */
    @GetMapping("/queryByCode")
    public WorkOrderInfoByCodeResponseDTO queryByCode(@RequestParam("orderCode") String orderCode) {
        return tGrWorkOrderService.queryByCode(orderCode);
    }

}