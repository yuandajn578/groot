package com.choice.cloud.architect.groot.service.inner.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;

import com.choice.cloud.architect.groot.convertor.IterActionConvertor;
import com.choice.cloud.architect.groot.dao.TGrIterationDao;
import com.choice.cloud.architect.groot.dto.iteration.CreateIterActionRequestDTO;
import com.choice.cloud.architect.groot.dto.iteration.InfoIterActionResponseDTO;
import com.choice.cloud.architect.groot.dto.iteration.PageListIterActionRequestDTO;
import com.choice.cloud.architect.groot.dto.iteration.PageListIterActionResponseDTO;
import com.choice.cloud.architect.groot.dto.iteration.SaveIterActionRequestDTO;
import com.choice.cloud.architect.groot.enums.IsDeletedFlagEnum;
import com.choice.cloud.architect.groot.enums.IterationStatusEnum;
import com.choice.cloud.architect.groot.exception.ServiceException;
import com.choice.cloud.architect.groot.model.TGrIteration;
import com.choice.cloud.architect.groot.model.TGrProject;
import com.choice.cloud.architect.groot.option.WebPage;
import com.choice.cloud.architect.groot.response.code.SysResponseCode;
import com.choice.cloud.architect.groot.service.inner.TGrIterationService;
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
 * 迭代表(TGrIteration)表服务实现类
 *
 * @author makejava
 * @since 2020-08-19 16:28:51
 */
@Service("tGrIterationService")
public class TGrIterationServiceImpl implements TGrIterationService {
    @Resource
    private TGrIterationDao tGrIterationDao;

    @Autowired
    private IterActionConvertor iterActionConvertor;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public TGrIteration queryById(Long id) {
        return this.tGrIterationDao.queryById(id);
    }

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    @Override
    public List<TGrIteration> queryAllByLimit(int offset, int limit) {
        return this.tGrIterationDao.queryAllByLimit(offset, limit);
    }

    /**
     * 新增数据
     *
     * @param tGrIteration 实例对象
     * @return 实例对象
     */
    @Override
    public TGrIteration insert(TGrIteration tGrIteration) {
        this.tGrIterationDao.insert(tGrIteration);
        return tGrIteration;
    }

    /**
     * 修改数据
     *
     * @param tGrIteration 实例对象
     * @return 实例对象
     */
    @Override
    public TGrIteration update(TGrIteration tGrIteration) {
        this.tGrIterationDao.update(tGrIteration);
        return this.queryById(tGrIteration.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {
        return this.tGrIterationDao.deleteById(id) > 0;
    }

    /**
     * 新增迭代
     *
     * @param request
     * @param authUser
     * @return
     */
    @Override
    public TGrIteration create(CreateIterActionRequestDTO request, AuthUser authUser) {
        String uid = authUser.getUid();
        // 获取当前登录人名字
        String currentUserName = CommonUtil.currentUserId();

        // 生成入库实体
        TGrIteration iterationDb = this.assembleIteration(request, uid, currentUserName);

        // 落库
        tGrIterationDao.insertDynamic(iterationDb);

        return iterationDb;
    }

    private TGrIteration assembleIteration(CreateIterActionRequestDTO request, String currentUseId, String currentUserName) {
        TGrIteration iteration = new TGrIteration();
        iteration.setOid(UUIDUtil.getUUID());
        // todo 这里保留这两个字段
        //iteration.setProjectId(request.getProjectId());
        //iteration.setProjectName(request.getProjectName());
        iteration.setProjectId("projectid");
        iteration.setProjectName("projectname");
        iteration.setIterationName(request.getIterationName());
        iteration.setIterationDesc(request.getIterationDesc());
        iteration.setExceptCompleteTime(request.getExceptCompleteTime());
        iteration.setActualCompleteTime(request.getActualCompleteTime());
        iteration.setIterationStatus(IterationStatusEnum.UN_EXECUTED.getCode());
        // 设置基础信息
        iteration.setCUser(currentUserName);
        iteration.setCTime(LocalDateTime.now());
        iteration.setUUser(currentUserName);
        iteration.setUTime(LocalDateTime.now());
        iteration.setIsDeleted(IsDeletedFlagEnum.NOT_DELETED.getCode());
        return iteration;
    }

    /**
     * 分页查询迭代
     *
     * @param request
     * @param authUser
     * @return
     */
    @Override
    public WebPage<List<PageListIterActionResponseDTO>> pageList(PageListIterActionRequestDTO request, AuthUser authUser) {
        Integer pageNum = request.getPageNum();
        Integer pageSize = request.getPageSize();
        // 分页查询工单列表
        Page<Object> page = PageHelper.startPage(pageNum, pageSize);
        List<TGrIteration> iterationList = tGrIterationDao.queryAllByPage(request);
        long total = page.getTotal();
        PageHelper.clearPage();

        if (CollectionUtils.isEmpty(iterationList)) {
            return new WebPage<>(pageNum, pageSize, 0, null);
        }

        // List<TGrIteration>转为List<PageListIterActionResponseDTO>
        List<PageListIterActionResponseDTO> responseDTOList = iterActionConvertor.pageListDo2Response(iterationList);

        return new WebPage<>(pageNum, pageSize, total, responseDTOList);
    }

    /**
     * 迭代详情
     *
     * @param oid
     * @return
     */
    @Override
    public InfoIterActionResponseDTO info(String oid) {
        if (StringUtils.isBlank(oid)) {
            throw new ServiceException(SysResponseCode.PARAM_ILLEGAL);
        }
        InfoIterActionResponseDTO responseDTO = new InfoIterActionResponseDTO();

        TGrIteration iteration = tGrIterationDao.queryByOid(oid);
        if (Objects.isNull(iteration)) {
            return responseDTO;
        }
        return iterActionConvertor.do2Response(iteration);
    }

    /**
     * 编辑保存迭代
     *
     * @param request
     * @param authUser
     */
    @Override
    public void save(SaveIterActionRequestDTO request, AuthUser authUser) {
        TGrIteration iteration = new TGrIteration();
        String currentUserName = CommonUtil.currentUserId();
        BeanUtils.copyProperties(request, iteration);
        if (StringUtils.isBlank(iteration.getOid())) {
            throw new ServiceException("oid is null");
        }
        iteration.setUUser(currentUserName);
        iteration.setUTime(LocalDateTime.now());
        if (tGrIterationDao.update(iteration) < 1) {
            throw new ServiceException("保存失败");
        }
    }
}