package com.choice.cloud.architect.groot.service.inner.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;

import com.choice.cloud.architect.groot.convertor.ProjectConvertor;
import com.choice.cloud.architect.groot.dao.TGrProjectDao;
import com.choice.cloud.architect.groot.dto.project.CreateProjectRequestDTO;
import com.choice.cloud.architect.groot.dto.project.InfoProjectResponseDTO;
import com.choice.cloud.architect.groot.dto.project.PageListProjectRequestDTO;
import com.choice.cloud.architect.groot.dto.project.PageListProjectResponseDTO;
import com.choice.cloud.architect.groot.dto.project.SaveProjectRequestDTO;
import com.choice.cloud.architect.groot.enums.IsDeletedFlagEnum;
import com.choice.cloud.architect.groot.exception.ServiceException;
import com.choice.cloud.architect.groot.model.TGrProject;
import com.choice.cloud.architect.groot.option.WebPage;
import com.choice.cloud.architect.groot.response.code.SysResponseCode;
import com.choice.cloud.architect.groot.service.inner.TGrProjectService;
import com.choice.cloud.architect.groot.util.CommonUtil;
import com.choice.cloud.architect.groot.util.UUIDUtil;
import com.choice.driver.jwt.entity.AuthUser;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 项目表(TGrProject)表服务实现类
 *
 * @author makejava
 * @since 2020-08-19 16:28:54
 */
@Service("tGrProjectService")
public class TGrProjectServiceImpl implements TGrProjectService {
    @Resource
    private TGrProjectDao tGrProjectDao;

    @Autowired
    private ProjectConvertor projectConvertor;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public TGrProject queryById(Long id) {
        return this.tGrProjectDao.queryById(id);
    }

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    @Override
    public List<TGrProject> queryAllByLimit(int offset, int limit) {
        return this.tGrProjectDao.queryAllByLimit(offset, limit);
    }

    /**
     * 新增数据
     *
     * @param tGrProject 实例对象
     * @return 实例对象
     */
    @Override
    public TGrProject insert(TGrProject tGrProject) {
        this.tGrProjectDao.insert(tGrProject);
        return tGrProject;
    }

    /**
     * 修改数据
     *
     * @param tGrProject 实例对象
     * @return 实例对象
     */
    @Override
    public TGrProject update(TGrProject tGrProject) {
        this.tGrProjectDao.update(tGrProject);
        return this.queryById(tGrProject.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {
        return this.tGrProjectDao.deleteById(id) > 0;
    }

    /**
     * 新增项目
     *
     * @param request
     * @param authUser
     * @return
     */
    @Override
    public TGrProject create(CreateProjectRequestDTO request, AuthUser authUser) {
        String uid = authUser.getUid();
        // 获取当前登录人名字
        String currentUserName = CommonUtil.currentUserId();

        // 生成入库实体
        TGrProject projectDb = this.assembleProject(request, uid, currentUserName);

        // 落库
        tGrProjectDao.insert(projectDb);

        return projectDb;
    }

    private TGrProject assembleProject(CreateProjectRequestDTO request, String uid, String currentUserName) {
        TGrProject project = new TGrProject();
        project.setOid(UUIDUtil.getUUID());
        project.setProjectName(request.getProjectName());
        project.setProjectDesc(request.getProjectDesc());
        project.setOwnerId(request.getOwnerId());
        project.setOwnerName(request.getOwnerName());
        project.setBackUpOwnerId(request.getBackUpOwnerId());
        project.setBackUpOwnerName(request.getBackUpOwnerName());
        // 设置基础信息
        project.setCUser(currentUserName);
        project.setCTime(LocalDateTime.now());
        project.setUUser(currentUserName);
        project.setUTime(LocalDateTime.now());
        project.setIsDeleted(IsDeletedFlagEnum.NOT_DELETED.getCode());
        return project;
    }

    /**
     * 分页查询项目
     *
     * @param request
     * @param authUser
     * @return
     */
    @Override
    public WebPage<List<PageListProjectResponseDTO>> pageList(PageListProjectRequestDTO request, AuthUser authUser) {
        Integer pageNum = request.getPageNum();
        Integer pageSize = request.getPageSize();
        // 分页查询工单列表
        Page<TGrProject> page = PageHelper.startPage(pageNum, pageSize);
        List<TGrProject> projectList = tGrProjectDao.queryAllByPage(request);
        long total = page.getTotal();
        PageHelper.clearPage();

        if (CollectionUtils.isEmpty(projectList)) {
            return new WebPage<>(pageNum, pageSize, 0, null);
        }

        // List<TGrProject>转为List<PageListProjectResponseDTO>
        List<PageListProjectResponseDTO> responseDTOList = projectConvertor.pageListDo2Response(projectList);

        return new WebPage<>(pageNum, pageSize, total, responseDTOList);
    }

    /**
     * 查看项目详情
     *
     * @param oid
     * @return
     */
    @Override
    public InfoProjectResponseDTO info(String oid) {
        if (StringUtils.isBlank(oid)) {
            throw new ServiceException(SysResponseCode.PARAM_ILLEGAL);
        }
        InfoProjectResponseDTO responseDTO = new InfoProjectResponseDTO();

        TGrProject project = tGrProjectDao.queryByOid(oid);
        if (Objects.isNull(project)) {
            return responseDTO;
        }
        return projectConvertor.do2Response(project);
    }

    /**
     * 编辑保存项目
     *
     * @param request
     * @param authUser
     */
    @Override
    public void save(SaveProjectRequestDTO request, AuthUser authUser) {
        TGrProject project = new TGrProject();
        BeanUtils.copyProperties(request, project);
        if (StringUtils.isBlank(project.getOid())) {
            throw new ServiceException("oid is null");
        }
        if (tGrProjectDao.update(project) < 1) {
            throw new ServiceException("保存失败");
        }
    }
}