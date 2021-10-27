package com.choice.cloud.architect.groot.service.workordersystem.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;

import com.choice.cloud.architect.groot.convertor.WorkOrderConvertor;
import com.choice.cloud.architect.groot.dao.workordersystem.TGrWorkOrderDao;
import com.choice.cloud.architect.groot.dto.workordersystem.ActiveCloseWorkOrderRequestDTO;
import com.choice.cloud.architect.groot.dto.workordersystem.ConfirmWorkOrderRequestDTO;
import com.choice.cloud.architect.groot.dto.workordersystem.CreateWorkOrderRequestDTO;
import com.choice.cloud.architect.groot.dto.workordersystem.HandleFinishWorkOrderRequestDTO;
import com.choice.cloud.architect.groot.dto.workordersystem.HandleRejectWorkOrderRequestDTO;
import com.choice.cloud.architect.groot.dto.workordersystem.PageListWorkOrderRequestDTO;
import com.choice.cloud.architect.groot.dto.workordersystem.PageListWorkOrderResponseDTO;
import com.choice.cloud.architect.groot.dto.workordersystem.StartHandleWorkOrderRequestDTO;
import com.choice.cloud.architect.groot.dto.workordersystem.WorkOrderInfoByCodeResponseDTO;
import com.choice.cloud.architect.groot.enums.IsDeletedFlagEnum;
import com.choice.cloud.architect.groot.enums.WorkOrderStatusEnum;
import com.choice.cloud.architect.groot.exception.ServiceException;
import com.choice.cloud.architect.groot.model.workordersystem.TGrWorkOrder;
import com.choice.cloud.architect.groot.option.WebPage;
import com.choice.cloud.architect.groot.process.service.MessageCenterProcessFeignClient;
import com.choice.cloud.architect.groot.response.ResponseData;
import com.choice.cloud.architect.groot.response.code.WorkOrderResponseCode;
import com.choice.cloud.architect.groot.service.workordersystem.TGrWorkOrderService;
import com.choice.cloud.architect.groot.util.CommonUtil;
import com.choice.cloud.architect.groot.util.UUIDUtil;
import com.choice.driver.jwt.entity.AuthUser;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * 工单表(TGrWorkOrder)表服务实现类
 *
 * @author makejava
 * @since 2020-08-10 11:02:15
 */
@Slf4j
@Service("tGrWorkOrderService")
public class TGrWorkOrderServiceImpl implements TGrWorkOrderService {

    @Value("${work.order.handle.userids}")
    private String orderReceiveUserStr;

    @Resource
    private TGrWorkOrderDao tGrWorkOrderDao;
    @Autowired
    private WorkOrderConvertor workOrderConvertor;
    @Autowired
    private MessageCenterProcessFeignClient messageCenterProcessFeignClient;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public TGrWorkOrder queryById(Long id) {
        return this.tGrWorkOrderDao.queryById(id);
    }

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    @Override
    public List<TGrWorkOrder> queryAllByLimit(int offset, int limit) {
        return this.tGrWorkOrderDao.queryAllByLimit(offset, limit);
    }

    /**
     * 新增数据
     *
     * @param tGrWorkOrder 实例对象
     * @return 实例对象
     */
    @Override
    public TGrWorkOrder insert(TGrWorkOrder tGrWorkOrder) {
        this.tGrWorkOrderDao.insert(tGrWorkOrder);
        return tGrWorkOrder;
    }

    /**
     * 修改数据
     *
     * @param tGrWorkOrder 实例对象
     * @return 实例对象
     */
    @Override
    public TGrWorkOrder update(TGrWorkOrder tGrWorkOrder) {
        this.tGrWorkOrderDao.update(tGrWorkOrder);
        return this.queryById(tGrWorkOrder.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {
        return this.tGrWorkOrderDao.deleteById(id) > 0;
    }

    /**
     * 分页查询工单列表
     *
     * @param request
     * @return
     */
    @Override
    public WebPage<List<PageListWorkOrderResponseDTO>> pageList(PageListWorkOrderRequestDTO request, AuthUser authUser) {
        Integer pageNum = request.getPageNum();
        Integer pageSize = request.getPageSize();
        // 分页查询工单列表
        Page<Object> page = PageHelper.startPage(pageNum, pageSize);
        List<TGrWorkOrder> workOrderList = tGrWorkOrderDao.queryAllByPage(request);
        long total = page.getTotal();
        PageHelper.clearPage();

        if (CollectionUtils.isEmpty(workOrderList)) {
            return new WebPage<>(pageNum, pageSize, 0, null);
        }

        // List<TGrWorkOrder>转为List<PageListWorkOrderResponseDTO>
        List<PageListWorkOrderResponseDTO> responseDTOList = workOrderConvertor.pageListDo2Response(workOrderList);

        // 设置用户菜单权限
        this.setUserPermission(authUser, responseDTOList);

        return new WebPage<>(pageNum, pageSize, total, responseDTOList);
    }

    private void setUserPermission(AuthUser authUser, List<PageListWorkOrderResponseDTO> responseDTOList) {
        boolean havePermission = false;

        if (StringUtils.isNotBlank(authUser.getUid()) && StringUtils.isNotBlank(orderReceiveUserStr)) {
            List<String> userIds = Splitter.on(",").splitToList(orderReceiveUserStr);
            if (CollectionUtils.isNotEmpty(userIds)
                && userIds.contains(authUser.getUid())) {
                havePermission = true;
            }
        }

        if (CollectionUtils.isNotEmpty(responseDTOList)) {
            boolean finalHavePermission = havePermission;
            responseDTOList.forEach(responseDTO -> {
                responseDTO.setHavePermission(finalHavePermission);
            });
        }
    }

    /**
     * 处理完成工单
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean handleFinish(HandleFinishWorkOrderRequestDTO request, AuthUser authUser) {
        @NotBlank(message = "工单编码不能为空") String orderCode = request.getOrderCode();
        @NotBlank(message = "工单处理反馈不能为空") String orderHandlerFeedback = request.getOrderHandlerFeedback();
        // 获取当前更新人名字
        String currentUserName = CommonUtil.currentUserId();
        // 根据工单编码查询对应的工单信息
        TGrWorkOrder tGrWorkOrder = tGrWorkOrderDao.queryByOrderCode(orderCode);
        if (Objects.isNull(tGrWorkOrder)) {
            log.error("根据工单编码没有查询到对应的工单信息");
            return false;
        }
        String orderStatus = tGrWorkOrder.getOrderStatus();
        if (!WorkOrderStatusEnum.ORDER_WAITING_HANDLE.getCode().equalsIgnoreCase(orderStatus)) {
            throw new ServiceException(WorkOrderResponseCode.NOT_FINISH);
        }
        // 处理完成订单
        boolean handleFlag =
            this.updateOrderStatus(tGrWorkOrder, authUser.getUid(), currentUserName, WorkOrderStatusEnum.ORDER_FINISH.getCode(), orderHandlerFeedback);
        // 发起工单申请人工作通知
        this.sendNoticeMessage(tGrWorkOrder, WorkOrderStatusEnum.ORDER_FINISH.getCode(), tGrWorkOrder.getOrderApplyUserId());
        return handleFlag;
    }

    /**
     * 根据工单编码更新工单状态
     * @param tGrWorkOrder
     * @param currentUserId
     * @param currentUserName
     * @param aimOrderStatusCode
     * @param orderHandlerFeedback
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateOrderStatus(TGrWorkOrder tGrWorkOrder, String currentUserId, String currentUserName, String aimOrderStatusCode, String orderHandlerFeedback) {
        // 更新工单状态
        TGrWorkOrder toUpdateOrder = new TGrWorkOrder();
        toUpdateOrder.setOrderCode(tGrWorkOrder.getOrderCode());
        toUpdateOrder.setOrderStatus(aimOrderStatusCode);
        toUpdateOrder.setUUser(currentUserName);
        toUpdateOrder.setUTime(LocalDateTime.now());
        if (StringUtils.isNoneBlank(currentUserId) && StringUtils.isNotBlank(orderHandlerFeedback)
            && StringUtils.isNotBlank(currentUserName)) {
            toUpdateOrder.setOrderHandlerUserId(currentUserId);
            toUpdateOrder.setOrderHandlerUser(currentUserName);
            toUpdateOrder.setOrderHandlerFeedback(orderHandlerFeedback);
        }

        int updateRet = tGrWorkOrderDao.update(toUpdateOrder);
        if (updateRet < 1) {
            log.error("【" + WorkOrderStatusEnum.getNameByCode(aimOrderStatusCode) + "】时更新工单状态失败");
            return false;
        }
        return true;
    }

    /**
     * 处理拒绝工单
     *
     * @param request
     * @return
     */
    @Override
    public boolean handleReject(HandleRejectWorkOrderRequestDTO request, AuthUser authUser) {
        @NotBlank(message = "工单编码不能为空") String orderCode = request.getOrderCode();
        @NotBlank(message = "工单处理反馈不能为空") String orderHandlerFeedback = request.getOrderHandlerFeedback();
        // 当前登陆人名称
        String currentUserName = CommonUtil.currentUserId();
        // 根据工单编码查询对应的工单信息
        TGrWorkOrder tGrWorkOrder = tGrWorkOrderDao.queryByOrderCode(orderCode);
        if (Objects.isNull(tGrWorkOrder)) {
            log.error("根据工单编码没有查询到对应的工单信息");
            return false;
        }
        String orderStatus = tGrWorkOrder.getOrderStatus();
        if (!WorkOrderStatusEnum.ORDER_WAITING_HANDLE.getCode().equalsIgnoreCase(orderStatus)) {
            throw new ServiceException(WorkOrderResponseCode.NOT_REJECT);
        }
        // 处理拒绝订单
        boolean handleFlag =
            this.updateOrderStatus(tGrWorkOrder, authUser.getUid(), currentUserName, WorkOrderStatusEnum.ORDER_REJECT.getCode(), orderHandlerFeedback);
        // 发起工单申请人工作通知
        this.sendNoticeMessage(tGrWorkOrder, WorkOrderStatusEnum.ORDER_REJECT.getCode(), tGrWorkOrder.getOrderApplyUserId());
        return handleFlag;
    }

    /**
     * 确认工单处理完成
     *
     * @param request
     */
    @Override
    public boolean confirm(ConfirmWorkOrderRequestDTO request, AuthUser authUser) {
        @NotBlank(message = "工单编码不能为空") String orderCode = request.getOrderCode();
        // 当前登陆人名称
        String currentUserName = CommonUtil.currentUserId();
        // 根据工单编码查询对应的工单信息
        TGrWorkOrder tGrWorkOrder = tGrWorkOrderDao.queryByOrderCode(orderCode);
        if (Objects.isNull(tGrWorkOrder)) {
            log.error("根据工单编码没有查询到对应的工单信息");
            return false;
        }
        String orderStatus = tGrWorkOrder.getOrderStatus();
        if (!WorkOrderStatusEnum.ORDER_FINISH.getCode().equalsIgnoreCase(orderStatus)) {
            throw new ServiceException(WorkOrderResponseCode.NOT_CONFIRM);
        }
        // 只能有发起工单人来进行点击确认按钮
        if (!tGrWorkOrder.getOrderApplyUserId().equals(authUser.getUid())) {
            throw new ServiceException(WorkOrderResponseCode.NOT_YOU_NOT_CONFIRM);
        }
        // 确认订单
        boolean handleFlag =
            this.updateOrderStatus(tGrWorkOrder, authUser.getUid(), currentUserName, WorkOrderStatusEnum.ORDER_HAVE_CONFIRMED.getCode(), "");
        return handleFlag;
    }

    /**
     * 主动关闭工单
     *
     * @param request
     */
    @Override
    public boolean activeClose(ActiveCloseWorkOrderRequestDTO request, AuthUser authUser) {
        @NotBlank(message = "工单编码不能为空") String orderCode = request.getOrderCode();
        // 当前登陆人名称
        String currentUserName = CommonUtil.currentUserId();
        // 根据工单编码查询对应的工单信息
        TGrWorkOrder tGrWorkOrder = tGrWorkOrderDao.queryByOrderCode(orderCode);
        if (Objects.isNull(tGrWorkOrder)) {
            log.error("根据工单编码没有查询到对应的工单信息");
            return false;
        }
        String orderStatus = tGrWorkOrder.getOrderStatus();
        if (!WorkOrderStatusEnum.ORDER_WAITING.getCode().equalsIgnoreCase(orderStatus)) {
            throw new ServiceException(WorkOrderResponseCode.NOT_CLOSE);
        }
        // 只能有发起工单人来进行点击关闭按钮
        if (!tGrWorkOrder.getOrderApplyUserId().equals(authUser.getUid())) {
            throw new ServiceException(WorkOrderResponseCode.NOT_YOU_NOT_CLOSE);
        }
        // 主动关闭订单
        boolean handleFlag =
            this.updateOrderStatus(tGrWorkOrder, authUser.getUid(), currentUserName, WorkOrderStatusEnum.ORDER_ACTIVE_CLOSE.getCode(), "");
        // todo 这里暂且从Apollo中获取对应的接收人
        if (StringUtils.isNotBlank(orderReceiveUserStr)) {
            // 发起工作通知
            this.sendNoticeMessage(tGrWorkOrder, WorkOrderStatusEnum.ORDER_ACTIVE_CLOSE.getCode(), orderReceiveUserStr);
        }
        return handleFlag;
    }

    /**
     * 接单
     *
     * @param request
     * @param authUser
     * @return
     */
    @Override
    public boolean startHandle(StartHandleWorkOrderRequestDTO request, AuthUser authUser) {
        @NotBlank(message = "工单编码不能为空") String orderCode = request.getOrderCode();
        // 当前登陆人名称
        String currentUserName = CommonUtil.currentUserId();
        // 根据工单编码查询对应的工单信息
        TGrWorkOrder tGrWorkOrder = tGrWorkOrderDao.queryByOrderCode(orderCode);
        if (Objects.isNull(tGrWorkOrder)) {
            log.error("根据工单编码没有查询到对应的工单信息");
            return false;
        }
        String orderStatus = tGrWorkOrder.getOrderStatus();
        if (!WorkOrderStatusEnum.ORDER_WAITING.getCode().equalsIgnoreCase(orderStatus)) {
            throw new ServiceException(WorkOrderResponseCode.NOT_ORDER);
        }
        // 主动关闭订单
        boolean handleFlag =
            this.updateOrderStatus(tGrWorkOrder, authUser.getUid(), currentUserName, WorkOrderStatusEnum.ORDER_WAITING_HANDLE.getCode(), "");
        return handleFlag;
    }

    /**
     * 新增工单
     *
     * @param request
     * @return
     */
    @Override
    public TGrWorkOrder create(CreateWorkOrderRequestDTO request, AuthUser authUser) {
        // 获取当前登录人名字
        String currentUserName = CommonUtil.currentUserId();

        // 生成入库实体
        TGrWorkOrder workOrderDb = this.assembleWorkOrder(request, authUser.getUid(), currentUserName);

        // 落库
        tGrWorkOrderDao.insert(workOrderDb);

        // todo 这里暂且从Apollo中获取对应的接收人
        if (StringUtils.isNotBlank(orderReceiveUserStr)) {
            // 发起工作通知
            this.sendNoticeMessage(workOrderDb, workOrderDb.getOrderStatus(), orderReceiveUserStr);
        }

        return workOrderDb;
    }

    /**
     * 发起工作通知
     * @param workOrderDb
     * @param orderStatus
     * @param handleUserIds
     */
    private void sendNoticeMessage(TGrWorkOrder workOrderDb, String orderStatus, String handleUserIds) {
        OapiMessageCorpconversationAsyncsendV2Request request = new OapiMessageCorpconversationAsyncsendV2Request();
        // 工作通知接收人
        request.setUseridList(handleUserIds);
        OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
        msg.setMsgtype("markdown");
        msg.setMarkdown(new OapiMessageCorpconversationAsyncsendV2Request.Markdown());
        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append("**工单名称**：").append(workOrderDb.getOrderName()).append("  \n  ");
        contentBuilder.append("**工单归属环境**：").append(workOrderDb.getOrderBelongEnv()).append("  \n  ");
        contentBuilder.append("**工单发起人**：").append(workOrderDb.getOrderApplyUser()).append("  \n  ");
        contentBuilder.append("**工单发起原因**：").append(workOrderDb.getOrderApplyReason()).append("  \n  ");
        // 待接单
        if (WorkOrderStatusEnum.ORDER_WAITING.getCode().equals(orderStatus)) {
            contentBuilder.append("**工单描述**：").append("有待接单的工单，请前往平台接单").append("  \n  ");
            // 处理完成
        } else if (WorkOrderStatusEnum.ORDER_FINISH.getCode().equals(orderStatus)) {
            contentBuilder.append("**工单描述**：").append("你申请的工单已处理完成，请前往平台确认工单处理结果").append("  \n  ");
            // 处理拒绝
        } else if (WorkOrderStatusEnum.ORDER_REJECT.getCode().equals(orderStatus)) {
            contentBuilder.append("**工单描述**：").append("你申请的工单已驳回，请前往平台查看驳回原因").append("  \n  ");
            // 主动关闭
        } else if (WorkOrderStatusEnum.ORDER_ACTIVE_CLOSE.getCode().equals(orderStatus)) {
            contentBuilder.append("**工单描述**：").append("申请的工单已主动关闭，无需处理该工单").append("  \n  ");
        }
        msg.getMarkdown().setText(contentBuilder.toString());
        msg.getMarkdown().setTitle("工单申请通知");
        // 工作通知内容
        request.setMsg(msg);
        log.info("【工单】，发起钉钉工作通知内容：{}", contentBuilder.toString());
        ResponseData<String> res = messageCenterProcessFeignClient.sendCorporationNotice(request);
        log.info("【工单】，发起钉钉工作通知调用结果： {}", res.getMsg());
    }

    private TGrWorkOrder assembleWorkOrder(CreateWorkOrderRequestDTO request, String currentUserId, String currentUserName) {
        TGrWorkOrder insertOrder = new TGrWorkOrder();

        insertOrder.setOrderCode(UUIDUtil.getUUID());
        insertOrder.setOrderName(request.getOrderName());
        insertOrder.setOrderTypeCode(request.getOrderTypeCode());
        insertOrder.setOrderTypeName(request.getOrderTypeName());
        insertOrder.setOrderStatus(WorkOrderStatusEnum.ORDER_WAITING.getCode());
        insertOrder.setOrderBelongEnv(Joiner.on(",").join(request.getEnvList().iterator()));
        insertOrder.setOrderApplyUser(currentUserName);
        insertOrder.setOrderApplyUserId(currentUserId);
        insertOrder.setOrderApplyReason(request.getOrderApplyReason());

        // 基础信息
        insertOrder.setCUser(currentUserName);
        insertOrder.setCTime(LocalDateTime.now());
        insertOrder.setUUser(currentUserName);
        insertOrder.setUTime(LocalDateTime.now());
        insertOrder.setIsDeleted(IsDeletedFlagEnum.NOT_DELETED.getCode());

        return insertOrder;
    }

    /**
     * 根据工单编码查看工单信息
     *
     * @param orderCode
     * @return
     */
    @Override
    public WorkOrderInfoByCodeResponseDTO queryByCode(String orderCode) {
        Assert.hasLength(orderCode, "工单编码不能为空");
        WorkOrderInfoByCodeResponseDTO responseDTO = new WorkOrderInfoByCodeResponseDTO();
        TGrWorkOrder workOrder = tGrWorkOrderDao.queryByOrderCode(orderCode);
        if (Objects.isNull(workOrder)) {
            return responseDTO;
        }
        responseDTO = workOrderConvertor.do2Response(workOrder);
        return responseDTO;
    }
}