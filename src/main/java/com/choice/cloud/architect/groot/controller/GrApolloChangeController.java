package com.choice.cloud.architect.groot.controller;

import java.util.List;

import com.choice.cloud.architect.groot.dto.ListApolloChangeDTO;
import com.choice.cloud.architect.groot.dto.ListApolloUpdateItemRequestDTO;
import com.choice.cloud.architect.groot.dto.ListApolloUpdateItemResponseDTO;
import com.choice.cloud.architect.groot.dto.PageListApolloCommitHistoryRequestDTO;
import com.choice.cloud.architect.groot.dto.PageListApolloCommitHistoryResponseDTO;
import com.choice.cloud.architect.groot.model.GrApolloChangeOrder;
import com.choice.cloud.architect.groot.request.CreateApolloChangeRequest;
import com.choice.cloud.architect.groot.request.DeleteApolloChangeRequest;
import com.choice.cloud.architect.groot.request.ListApolloChangeRequest;
import com.choice.cloud.architect.groot.request.PublishApolloChangeRequest;
import com.choice.cloud.architect.groot.request.UpdateApolloChangeRequest;
import com.choice.cloud.architect.groot.response.Page;
import com.choice.cloud.architect.groot.response.ResponseData;
import com.choice.cloud.architect.groot.service.inner.GrApolloChangeService;
import com.choice.driver.jwt.entity.AuthUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(description = "阿波罗配置项变更",tags = "阿波罗配置项变更")
@RestController
@RequestMapping("/api/apolloChange")
@Slf4j
public class GrApolloChangeController {

    @Autowired
    private GrApolloChangeService grApolloChangeService;

    @ApiOperation(value = "创建阿波罗变更单，存在抛异常",notes = "创建阿波罗变更单，存在抛异常")
    @PostMapping("/create")
    public ResponseData createChange(@RequestBody @Validated CreateApolloChangeRequest createApolloChangeRequest, AuthUser authUser){
        grApolloChangeService.createChange(createApolloChangeRequest,authUser);
        return ResponseData.createBySuccess();
    }

    @ApiOperation(value = "主键查询阿波罗变更单",notes = "主键查询阿波罗变更单")
    @GetMapping("/query")
    public ResponseData queryChange(@RequestParam("oid") String oid){
        GrApolloChangeOrder grApolloChangeOrder = grApolloChangeService.queryChange(oid);
        return ResponseData.createBySuccess(grApolloChangeOrder);
    }

    @ApiOperation(value = "修改阿波罗变更单，不存在就创建",notes = "修改阿波罗变更单，不存在就创建")
    @PostMapping("/update")
    public ResponseData updateOrCreateChange(@RequestBody @Validated UpdateApolloChangeRequest updateApolloChangeRequest, AuthUser authUser){
        grApolloChangeService.updateOrCreateChange(updateApolloChangeRequest,authUser);
        return ResponseData.createBySuccess();
    }

    @ApiOperation(value = "删除阿波罗配置项",notes = "删除阿波罗配置项")
    @PostMapping("/delete")
    public ResponseData deleteChange(@RequestBody @Validated DeleteApolloChangeRequest deleteApolloChangeRequest, AuthUser authUser){
        grApolloChangeService.deleteChange(deleteApolloChangeRequest,authUser);
        return ResponseData.createBySuccess();
    }


    @ApiOperation(value = "发布阿波罗配置",notes = "发布阿波罗配置")
    @PostMapping("/publish")
    public ResponseData publishChange(@RequestBody @Validated PublishApolloChangeRequest publishApolloChangeRequest, AuthUser authUser){
        grApolloChangeService.publishChange(publishApolloChangeRequest,authUser);
        return ResponseData.createBySuccess();
    }

    @ApiOperation(value = "阿波罗变更单查询(非分页)",notes = "阿波罗变更单查询(非分页)")
    @PostMapping("/list")
    public ResponseData<List<ListApolloChangeDTO>> list(@RequestBody @Validated ListApolloChangeRequest listChangeRequest, AuthUser authUser) {
        List<ListApolloChangeDTO> list = grApolloChangeService.list(listChangeRequest,authUser);
        Page page = new Page();
        page.setPageNum(0);
        page.setPageSize(list.size());
        page.setTotal(list.size());
        return ResponseData.createBySuccess(page, list);
    }

    @ApiOperation(value = "阿波罗修改的配置列表",notes = "阿波罗修改的配置列表")
    @PostMapping("/listUpdateItem")
    public ResponseData<List<ListApolloUpdateItemResponseDTO>> listUpdateItem(@RequestBody @Validated ListApolloUpdateItemRequestDTO request) {
        List<ListApolloUpdateItemResponseDTO> responseDTOList = grApolloChangeService.listUpdateItem(request);
        return ResponseData.createBySuccess(responseDTOList);
    }

    /**
     * PS:
     * 1、Apollo原生的变更历史采用的是瀑布流的形式
     * 且分页数据没有返回total字段
     * 2、前端框架不支持PC端瀑布流的样式；
     * 且如果采用分页的话，没有total字段也不能够分页
     * 这里暂且查看100条数据
     * @param request
     * @return
     */
    @ApiOperation(value = "分页查询阿波罗配置项更改历史",notes = "分页查询阿波罗配置项更改历史")
    @PostMapping("/pageListCommitHistoryItem")
    public ResponseData<List<PageListApolloCommitHistoryResponseDTO>> pageListCommitHistoryItem(@RequestBody @Validated PageListApolloCommitHistoryRequestDTO request) {
        List<PageListApolloCommitHistoryResponseDTO> responseDTOList = grApolloChangeService.pageListCommitHistoryItem(request);
        return ResponseData.createBySuccess(responseDTOList);
    }
}
