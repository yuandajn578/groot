package com.choice.cloud.architect.groot.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

import com.choice.cloud.architect.groot.dto.iteration.CreateIterActionRequestDTO;
import com.choice.cloud.architect.groot.dto.iteration.InfoIterActionResponseDTO;
import com.choice.cloud.architect.groot.dto.iteration.PageListIterActionRequestDTO;
import com.choice.cloud.architect.groot.dto.iteration.PageListIterActionResponseDTO;
import com.choice.cloud.architect.groot.dto.iteration.SaveIterActionRequestDTO;
import com.choice.cloud.architect.groot.dto.project.InfoProjectResponseDTO;
import com.choice.cloud.architect.groot.dto.project.SaveProjectRequestDTO;
import com.choice.cloud.architect.groot.model.TGrIteration;
import com.choice.cloud.architect.groot.option.WebPage;
import com.choice.cloud.architect.groot.response.Page;
import com.choice.cloud.architect.groot.response.ResponseData;
import com.choice.cloud.architect.groot.service.inner.TGrIterationService;
import com.choice.driver.jwt.entity.AuthUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 迭代表(TGrIteration)表控制层
 *
 * @author makejava
 * @since 2020-08-19 16:28:52
 */
@RestController
@RequestMapping("/iteration")
public class TGrIterationController {
    /**
     * 服务对象
     */
    @Resource
    private TGrIterationService tGrIterationService;

    /**
     * 新增迭代
     * @param request
     * @param authUser
     * @return
     */
    @PostMapping("/create")
    public ResponseData<TGrIteration> create(@RequestBody @Valid CreateIterActionRequestDTO request, AuthUser authUser) {
        return ResponseData.createBySuccess(tGrIterationService.create(request, authUser));
    }

    /**
     * 分页查询迭代
     * @param request
     * @param authUser
     * @return
     */
    @PostMapping("/pageList")
    public ResponseData<List<PageListIterActionResponseDTO>> pageList(@RequestBody @Valid PageListIterActionRequestDTO request, AuthUser authUser) {
        WebPage<List<PageListIterActionResponseDTO>> pageList = tGrIterationService.pageList(request, authUser);
        Page page = new Page();
        page.setPageNum(pageList.getPageNum());
        page.setPageSize(pageList.getPageSize());
        page.setTotal(pageList.getTotal().intValue());
        return ResponseData.createBySuccess(page, pageList.getData());
    }

    /**
     * 查询迭代
     * @param oid
     * @return
     */
    @GetMapping("/info")
    public ResponseData<InfoIterActionResponseDTO> info(@RequestParam @Valid String oid) {
        return ResponseData.createBySuccess(tGrIterationService.info(oid));
    }

    /**
     * 编辑保存迭代
     * @param request
     * @param authUser
     * @return
     */
    @PostMapping("/edit/save")
    public ResponseData save(@RequestBody @Valid SaveIterActionRequestDTO request, AuthUser authUser) {
        tGrIterationService.save(request, authUser);
        return ResponseData.createBySuccess();
    }
}