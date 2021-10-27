package com.choice.cloud.architect.groot.service.workordersystem.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.choice.cloud.architect.groot.dao.workordersystem.TGrWorkOrderTypeDao;
import com.choice.cloud.architect.groot.dto.workordersystem.CreateWorkOrderTypeRequestDTO;
import com.choice.cloud.architect.groot.dto.workordersystem.ListWorkOrderTypeResponseDTO;
import com.choice.cloud.architect.groot.enums.IsDeletedFlagEnum;
import com.choice.cloud.architect.groot.model.workordersystem.TGrWorkOrderType;
import com.choice.cloud.architect.groot.service.workordersystem.TGrWorkOrderTypeService;
import com.choice.cloud.architect.groot.util.CommonUtil;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

/**
 * 工单类型表(TGrWorkOrderType)表服务实现类
 *
 * @author LZ
 * @since 2020-08-10 11:02:18
 */
@Service("tGrWorkOrderTypeService")
public class TGrWorkOrderTypeServiceImpl implements TGrWorkOrderTypeService {
    @Resource
    private TGrWorkOrderTypeDao tGrWorkOrderTypeDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public TGrWorkOrderType queryById(Long id) {
        return this.tGrWorkOrderTypeDao.queryById(id);
    }

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    @Override
    public List<TGrWorkOrderType> queryAllByLimit(int offset, int limit) {
        return this.tGrWorkOrderTypeDao.queryAllByLimit(offset, limit);
    }

    /**
     * 新增数据
     *
     * @param tGrWorkOrderType 实例对象
     * @return 实例对象
     */
    @Override
    public TGrWorkOrderType insert(TGrWorkOrderType tGrWorkOrderType) {
        this.tGrWorkOrderTypeDao.insert(tGrWorkOrderType);
        return tGrWorkOrderType;
    }

    /**
     * 修改数据
     *
     * @param tGrWorkOrderType 实例对象
     * @return 实例对象
     */
    @Override
    public TGrWorkOrderType update(TGrWorkOrderType tGrWorkOrderType) {
        this.tGrWorkOrderTypeDao.update(tGrWorkOrderType);
        return this.queryById(tGrWorkOrderType.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {
        return this.tGrWorkOrderTypeDao.deleteById(id) > 0;
    }

    /**
     * 查询所有的工单类型
     *
     * @return
     */
    @Override
    public List<ListWorkOrderTypeResponseDTO> list() {
        List<ListWorkOrderTypeResponseDTO> responseDTOList = Lists.newArrayList();
        TGrWorkOrderType workOrderType = new TGrWorkOrderType();
        workOrderType.setIsDeleted(IsDeletedFlagEnum.NOT_DELETED.getCode());
        List<TGrWorkOrderType> workOrderTypeList = tGrWorkOrderTypeDao.queryAll(workOrderType);
        if (CollectionUtils.isEmpty(workOrderTypeList)) {
            return responseDTOList;
        }
        responseDTOList = workOrderTypeList.stream()
            .filter(Objects::nonNull)
            .map(orderType -> ListWorkOrderTypeResponseDTO.builder()
                .orderTypeCode(orderType.getOrderTypeCode())
                .orderTypeName(orderType.getOrderTypeName())
                .build())
            .collect(Collectors.toList());
        return responseDTOList;
    }

    /**
     * 新增工单类型
     *
     * @param request
     * @return
     */
    @Override
    public TGrWorkOrderType create(CreateWorkOrderTypeRequestDTO request) {
        // 获取当前登录人名字
        String currentUserName = CommonUtil.currentUserId();

        // 生成入库实体
        TGrWorkOrderType workOrderTypeDb = this.assembleWorkTypeOrder(request, currentUserName);

        // 落库
        tGrWorkOrderTypeDao.insert(workOrderTypeDb);

        return workOrderTypeDb;
    }

    private TGrWorkOrderType assembleWorkTypeOrder(CreateWorkOrderTypeRequestDTO request, String currentUserName) {
        TGrWorkOrderType insertOrderType = new TGrWorkOrderType();

        insertOrderType.setOrderTypeCode(request.getOrderTypeCode());
        insertOrderType.setOrderTypeName(request.getOrderTypeName());
        insertOrderType.setOrderTypeDesc(request.getOrderTypeDesc());
        insertOrderType.setOrderTypeBelongUserId(request.getOrderTypeBelongUserId());
        insertOrderType.setOrderTypeBelongUser(request.getOrderTypeBelongUser());

        // 基础信息
        insertOrderType.setCUser(currentUserName);
        insertOrderType.setCTime(LocalDateTime.now());
        insertOrderType.setUUser(currentUserName);
        insertOrderType.setUTime(LocalDateTime.now());
        insertOrderType.setIsDeleted(IsDeletedFlagEnum.NOT_DELETED.getCode());

        return insertOrderType;
    }
}