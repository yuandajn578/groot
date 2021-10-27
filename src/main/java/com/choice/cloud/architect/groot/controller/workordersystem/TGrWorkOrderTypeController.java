package com.choice.cloud.architect.groot.controller.workordersystem;

import java.util.List;

import javax.annotation.Resource;
import com.choice.cloud.architect.groot.dto.workordersystem.CreateWorkOrderTypeRequestDTO;
import com.choice.cloud.architect.groot.dto.workordersystem.ListWorkOrderTypeResponseDTO;
import com.choice.cloud.architect.groot.model.workordersystem.TGrWorkOrderType;
import com.choice.cloud.architect.groot.response.ResponseData;
import com.choice.cloud.architect.groot.service.workordersystem.TGrWorkOrderTypeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 工单类型表(TGrWorkOrderType)表控制层
 *
 * @author LZ
 * @since 2020-08-10 11:02:18
 */
@RestController
@RequestMapping("/workordertype")
public class TGrWorkOrderTypeController {
    /**
     * 服务对象
     */
    @Resource
    private TGrWorkOrderTypeService tGrWorkOrderTypeService;

    /**
     * 查询所有的工单类型
     * @return
     */
    @PostMapping("/list")
    public ResponseData<List<ListWorkOrderTypeResponseDTO>> list() {
        List<ListWorkOrderTypeResponseDTO> responseDTOList = tGrWorkOrderTypeService.list();
        return ResponseData.createBySuccess(responseDTOList);
    }

    /**
     * 新增工单类型
     * @param request
     * @return
     */
    @PostMapping("/create")
    public ResponseData<TGrWorkOrderType> create(@RequestBody CreateWorkOrderTypeRequestDTO request) {
        TGrWorkOrderType workOrderType = tGrWorkOrderTypeService.create(request);
        return ResponseData.createBySuccess(workOrderType);
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("selectOne")
    public TGrWorkOrderType selectOne(Long id) {
        return this.tGrWorkOrderTypeService.queryById(id);
    }

}