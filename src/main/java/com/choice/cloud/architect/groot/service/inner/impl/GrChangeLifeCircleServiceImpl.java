package com.choice.cloud.architect.groot.service.inner.impl;

import com.choice.cloud.architect.groot.dao.GrChangeLifeCircleMapper;
import com.choice.cloud.architect.groot.dto.GrChangeLifeCirclePageDTO;
import com.choice.cloud.architect.groot.enums.EnvTypeEnum;
import com.choice.cloud.architect.groot.enums.PublishTypeEnum;
import com.choice.cloud.architect.groot.model.GrChangeLifeCircle;
import com.choice.cloud.architect.groot.option.GlobalConst;
import com.choice.cloud.architect.groot.option.WebPage;
import com.choice.cloud.architect.groot.request.PageChangeLifeCircleRequest;
import com.choice.cloud.architect.groot.service.inner.GrChangeLifeCircleService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/13 19:09
 */
@Service
public class GrChangeLifeCircleServiceImpl extends BaseService implements GrChangeLifeCircleService {

    @Autowired
    private GrChangeLifeCircleMapper grChangeLifeCircleMapper;

    @Override
    public WebPage<List<GrChangeLifeCirclePageDTO>> pageList(PageChangeLifeCircleRequest request) {
        Integer pageNum = request.getPageNum();
        Integer pageSize = request.getPageSize();
        List<GrChangeLifeCirclePageDTO> changeLifeCirclePageDTOList = Lists.newArrayList();
        PageHelper.startPage(pageNum, pageSize);
        List<GrChangeLifeCircle> grChangeLifeCircleList = grChangeLifeCircleMapper.listWithPage(request.getChangeId(), request.getEnvType(), GlobalConst.NOT_DELETE);

        if (CollectionUtils.isEmpty(grChangeLifeCircleList)) {
            return new WebPage<>(pageNum, pageSize, 0, null);
        }

        for (GrChangeLifeCircle grChangeLifeCircle : grChangeLifeCircleList) {
            GrChangeLifeCirclePageDTO grChangeLifeCirclePageDTO = new GrChangeLifeCirclePageDTO();
            BeanUtils.copyProperties(grChangeLifeCircle, grChangeLifeCirclePageDTO);
//            grChangeLifeCirclePageDTO.setPublishStatus(PublishStatusEnum.description(grPublish.getPublishStatus()));
//            grChangeLifeCirclePageDTO.setTestStatus(TestStatusEnum.description(grPublish.getTestStatus()));
            changeLifeCirclePageDTOList.add(grChangeLifeCirclePageDTO);
        }

        PageInfo<GrChangeLifeCircle> info = new PageInfo<>(grChangeLifeCircleList);
        return new WebPage<>(pageNum, pageSize, info.getTotal(), changeLifeCirclePageDTOList);
    }

    @Override
    public Map<String, List<GrChangeLifeCirclePageDTO>> envTypeList(PageChangeLifeCircleRequest request) {
        Integer pageSize = request.getPageSize();
        String changeId = request.getChangeId();
        Map<String, List<GrChangeLifeCirclePageDTO>> resMap = Maps.newHashMap();
        for (EnvTypeEnum envTypeEnum : EnvTypeEnum.values()) {
            List<GrChangeLifeCircle> grChangeLifeCircleList = grChangeLifeCircleMapper.list(changeId, envTypeEnum.value(), pageSize, GlobalConst.NOT_DELETE);
            List<GrChangeLifeCirclePageDTO> changeLifeCirclePageDTOList = grChangeLifeCircleList.stream().map(grChangeLifeCircle -> {
                GrChangeLifeCirclePageDTO grChangeLifeCirclePageDTO = new GrChangeLifeCirclePageDTO();
                BeanUtils.copyProperties(grChangeLifeCircle, grChangeLifeCirclePageDTO);
                return grChangeLifeCirclePageDTO;
            }).collect(Collectors.toList());
            resMap.put(envTypeEnum.value(), changeLifeCirclePageDTOList);
        }
        return resMap;
    }

    @Override
    public int save(GrChangeLifeCircle grChangeLifeCircle) {
        return grChangeLifeCircleMapper.insert(grChangeLifeCircle);
    }
}
