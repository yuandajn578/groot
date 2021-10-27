package com.choice.cloud.architect.groot.convertor;

import java.util.List;

import com.choice.cloud.architect.groot.dto.workordersystem.PageListWorkOrderResponseDTO;
import com.choice.cloud.architect.groot.dto.workordersystem.WorkOrderInfoByCodeResponseDTO;
import com.choice.cloud.architect.groot.model.workordersystem.TGrWorkOrder;
import org.mapstruct.Mapper;

/**
 * <p>
 * 运维工单对象转换器
 * </p>
 *
 * @author LZ
 * @date Created in 2020/8/10 13:22
 */
@Mapper(componentModel = "spring")
public interface WorkOrderConvertor {
    List<PageListWorkOrderResponseDTO> pageListDo2Response(List<TGrWorkOrder> workOrderList);

    WorkOrderInfoByCodeResponseDTO do2Response(TGrWorkOrder workOrder);
}
