package com.choice.cloud.architect.groot.service.inner;

import java.util.List;

import com.choice.cloud.architect.groot.dto.project.CreateProjectRequestDTO;
import com.choice.cloud.architect.groot.dto.project.InfoProjectResponseDTO;
import com.choice.cloud.architect.groot.dto.project.PageListProjectRequestDTO;
import com.choice.cloud.architect.groot.dto.project.PageListProjectResponseDTO;
import com.choice.cloud.architect.groot.dto.project.SaveProjectRequestDTO;
import com.choice.cloud.architect.groot.model.TGrProject;
import com.choice.cloud.architect.groot.option.WebPage;
import com.choice.driver.jwt.entity.AuthUser;

/**
 * 项目表(TGrProject)表服务接口
 *
 * @author makejava
 * @since 2020-08-19 16:28:54
 */
public interface TGrProjectService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    TGrProject queryById(Long id);

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<TGrProject> queryAllByLimit(int offset, int limit);

    /**
     * 新增数据
     *
     * @param tGrProject 实例对象
     * @return 实例对象
     */
    TGrProject insert(TGrProject tGrProject);

    /**
     * 修改数据
     *
     * @param tGrProject 实例对象
     * @return 实例对象
     */
    TGrProject update(TGrProject tGrProject);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Long id);

    /**
     * 新增项目
     * @param request
     * @param authUser
     * @return
     */
    TGrProject create(CreateProjectRequestDTO request, AuthUser authUser);

    /**
     * 分页查询项目
     * @param request
     * @param authUser
     * @return
     */
    WebPage<List<PageListProjectResponseDTO>> pageList(PageListProjectRequestDTO request, AuthUser authUser);

    /**
     * 查看项目详情
     * @param oid
     * @return
     */
    InfoProjectResponseDTO info(String oid);

    /**
     * 编辑保存项目
     * @param request
     * @param authUser
     */
    void save(SaveProjectRequestDTO request, AuthUser authUser);
}