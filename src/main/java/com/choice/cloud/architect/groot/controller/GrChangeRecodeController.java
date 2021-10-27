package com.choice.cloud.architect.groot.controller;

import com.choice.cloud.architect.groot.convertor.ChangeRecordConvertor;
import com.choice.cloud.architect.groot.dto.ListApolloChangeDTO;
import com.choice.cloud.architect.groot.model.GrChangeRecode;
import com.choice.cloud.architect.groot.request.ChangeRecodeRequest;
import com.choice.cloud.architect.groot.response.Page;
import com.choice.cloud.architect.groot.response.ResponseData;
import com.choice.cloud.architect.groot.service.inner.GrChangeRecodeService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


/**
 * (GrChangeRecode)表控制层
 *
 * @author makejava
 * @since 2020-03-03 16:55:22
 */
@Api(description = "变更单操作日志",tags = "变更单操作日志")
@RestController
@RequestMapping("/grChangeRecode")
public class GrChangeRecodeController {
    /**
     * 服务对象
     */
    @Resource
    private GrChangeRecodeService grChangeRecodeService;

    @Autowired
    private ChangeRecordConvertor changeRecordConvertor;

    @ApiOperation(value = "变更单操作日志查询",notes = "变更单操作日志查询")
    @PostMapping("/list")
    public ResponseData<List<GrChangeRecode>> queryAll(@RequestBody ChangeRecodeRequest changeRecodeRequest) {
        GrChangeRecode grChangeRecode = changeRecordConvertor.recordReq2ChangeRecord(changeRecodeRequest);
        PageHelper.startPage(changeRecodeRequest.getPageNum(),changeRecodeRequest.getPageSize());
        List<GrChangeRecode> grChangeRecodes = grChangeRecodeService.queryAll(grChangeRecode);
        PageInfo pageInfo = new PageInfo(grChangeRecodes);
        Page page = new Page();
        page.setPageNum(pageInfo.getPageNum());
        page.setPageSize(pageInfo.getPageSize());
        page.setTotal((int)pageInfo.getTotal());
        return ResponseData.createBySuccess(page, pageInfo.getList());
    }
}