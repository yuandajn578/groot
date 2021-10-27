package com.choice.cloud.architect.groot.convertor;

import java.util.List;

import com.choice.cloud.architect.groot.dto.iteration.InfoIterActionResponseDTO;
import com.choice.cloud.architect.groot.dto.iteration.PageListIterActionResponseDTO;
import com.choice.cloud.architect.groot.dto.workordersystem.PageListWorkOrderResponseDTO;
import com.choice.cloud.architect.groot.dto.workordersystem.WorkOrderInfoByCodeResponseDTO;
import com.choice.cloud.architect.groot.model.TGrIteration;
import com.choice.cloud.architect.groot.model.workordersystem.TGrWorkOrder;
import org.mapstruct.Mapper;

/**
 * <p>
 * 迭代对象转换器
 * </p>
 *
 * @author LZ
 */
@Mapper(componentModel = "spring")
public interface IterActionConvertor {
    List<PageListIterActionResponseDTO> pageListDo2Response(List<TGrIteration> iterationList);

    InfoIterActionResponseDTO do2Response(TGrIteration iteration);
}
