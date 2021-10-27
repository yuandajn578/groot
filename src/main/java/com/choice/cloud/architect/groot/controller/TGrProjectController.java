package com.choice.cloud.architect.groot.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

import com.choice.cloud.architect.groot.dto.project.CreateProjectRequestDTO;
import com.choice.cloud.architect.groot.dto.project.InfoProjectResponseDTO;
import com.choice.cloud.architect.groot.dto.project.PageListProjectRequestDTO;
import com.choice.cloud.architect.groot.dto.project.PageListProjectResponseDTO;
import com.choice.cloud.architect.groot.dto.project.SaveProjectRequestDTO;
import com.choice.cloud.architect.groot.model.TGrProject;
import com.choice.cloud.architect.groot.option.WebPage;
import com.choice.cloud.architect.groot.response.Page;
import com.choice.cloud.architect.groot.response.ResponseData;
import com.choice.cloud.architect.groot.service.inner.TGrProjectService;
import com.choice.driver.jwt.entity.AuthUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 项目表(TGrProject)表控制层
 *
 * @author makejava
 * @since 2020-08-19 16:28:54
 */
@RestController
@RequestMapping("/project")
public class TGrProjectController {
    /**
     * 服务对象
     */
    @Resource
    private TGrProjectService tGrProjectService;

    /**
     * 新增项目
     * @param request
     * @param authUser
     * @return
     */
    @PostMapping("/create")
    public ResponseData<TGrProject> create(@RequestBody @Valid CreateProjectRequestDTO request, AuthUser authUser) {
        return ResponseData.createBySuccess(tGrProjectService.create(request, authUser));
    }

    /**
     * 分页查询项目
     * @param request
     * @param authUser
     * @return
     */
    @PostMapping("/pageList")
    public ResponseData<List<PageListProjectResponseDTO>> pageList(@RequestBody @Valid PageListProjectRequestDTO request, AuthUser authUser) {
        WebPage<List<PageListProjectResponseDTO>> pageList = tGrProjectService.pageList(request, authUser);
        Page page = new Page();
        page.setPageNum(pageList.getPageNum());
        page.setPageSize(pageList.getPageSize());
        page.setTotal(pageList.getTotal().intValue());
        return ResponseData.createBySuccess(page, pageList.getData());
    }

    /**
     * 查询项目
     * @param oid
     * @return
     */
    @GetMapping("/info")
    public ResponseData<InfoProjectResponseDTO> info(@RequestParam @Valid String oid) {
        return ResponseData.createBySuccess(tGrProjectService.info(oid));
    }

    /**
     * 编辑保存项目
     * @param request
     * @param authUser
     * @return
     */
    @PostMapping("/edit/save")
    public ResponseData save(@RequestBody @Valid SaveProjectRequestDTO request, AuthUser authUser) {
        tGrProjectService.save(request, authUser);
        return ResponseData.createBySuccess();
    }
}