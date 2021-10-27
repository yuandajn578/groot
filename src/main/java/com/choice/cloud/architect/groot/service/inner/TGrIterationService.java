package com.choice.cloud.architect.groot.service.inner;

import java.util.List;

import com.choice.cloud.architect.groot.dto.iteration.CreateIterActionRequestDTO;
import com.choice.cloud.architect.groot.dto.iteration.InfoIterActionResponseDTO;
import com.choice.cloud.architect.groot.dto.iteration.PageListIterActionRequestDTO;
import com.choice.cloud.architect.groot.dto.iteration.PageListIterActionResponseDTO;
import com.choice.cloud.architect.groot.dto.iteration.SaveIterActionRequestDTO;
import com.choice.cloud.architect.groot.dto.project.PageListProjectResponseDTO;
import com.choice.cloud.architect.groot.model.TGrIteration;
import com.choice.cloud.architect.groot.option.WebPage;
import com.choice.driver.jwt.entity.AuthUser;

/**
 * 迭代表(TGrIteration)表服务接口
 *
 * @author makejava
 * @since 2020-08-19 16:28:50
 */
public interface TGrIterationService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    TGrIteration queryById(Long id);

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<TGrIteration> queryAllByLimit(int offset, int limit);

    /**
     * 新增数据
     *
     * @param tGrIteration 实例对象
     * @return 实例对象
     */
    TGrIteration insert(TGrIteration tGrIteration);

    /**
     * 修改数据
     *
     * @param tGrIteration 实例对象
     * @return 实例对象
     */
    TGrIteration update(TGrIteration tGrIteration);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Long id);

    /**
     * 新增迭代
     * @param request
     * @param authUser
     * @return
     */
    TGrIteration create(CreateIterActionRequestDTO request, AuthUser authUser);

    /**
     * 分页查询迭代
     * @param request
     * @param authUser
     * @return
     */
    WebPage<List<PageListIterActionResponseDTO>> pageList(PageListIterActionRequestDTO request, AuthUser authUser);

    /**
     * 迭代详情
     * @param oid
     * @return
     */
    InfoIterActionResponseDTO info(String oid);

    /**
     * 编辑保存迭代
     * @param request
     * @param authUser
     */
    void save(SaveIterActionRequestDTO request, AuthUser authUser);
}