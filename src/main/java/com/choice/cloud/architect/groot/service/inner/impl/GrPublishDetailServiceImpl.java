package com.choice.cloud.architect.groot.service.inner.impl;

import com.choice.cloud.architect.groot.dao.GrPublishDetailMapper;
import com.choice.cloud.architect.groot.dto.ListPublishDetailDTO;
import com.choice.cloud.architect.groot.dto.PublishDetailCreateDTO;
import com.choice.cloud.architect.groot.dto.PublishPodDetailDTO;
import com.choice.cloud.architect.groot.exception.ServiceException;
import com.choice.cloud.architect.groot.model.GrPublishDetail;
import com.choice.cloud.architect.groot.service.inner.GrPublishDetailService;
import com.choice.driver.jwt.entity.AuthUser;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author zhangkun
 */
@Service
public class GrPublishDetailServiceImpl extends BaseService implements GrPublishDetailService {
    @Autowired
    private GrPublishDetailMapper grPublishDetailMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void savePublishDetail(PublishDetailCreateDTO publishDetailCreateDTO, AuthUser authUser) {
        if (StringUtils.isBlank(publishDetailCreateDTO.getPublishId())) {
            throw new ServiceException("发布单id为空");
        }

        if (CollectionUtils.isEmpty(publishDetailCreateDTO.getPublishPodDetails())) {
            return;
        }

        for (PublishPodDetailDTO publishPodDetail : publishDetailCreateDTO.getPublishPodDetails()) {
            GrPublishDetail grPublishDetail = new GrPublishDetail();
            initSaveModel(grPublishDetail, authUser);
            grPublishDetail.setPublishId(publishDetailCreateDTO.getPublishId());
            BeanUtils.copyProperties(publishPodDetail, grPublishDetail);

            grPublishDetailMapper.insert(grPublishDetail);
        }
    }

    @Override
    public List<ListPublishDetailDTO> listPublishDetail(String publishId) {
        if (StringUtils.isBlank(publishId)) {
            throw new ServiceException("发布单id为空");
        }

        List<ListPublishDetailDTO> listPublishDetailDTOList = Lists.newArrayList();
        List<GrPublishDetail> grPublishDetails = grPublishDetailMapper.selectByPublishId(publishId);

        if (grPublishDetails.isEmpty()) {
            return listPublishDetailDTOList;
        }

        for (GrPublishDetail grPublishDetail : grPublishDetails) {
            ListPublishDetailDTO listPublishDetailDTO = new ListPublishDetailDTO();

            BeanUtils.copyProperties(grPublishDetail, listPublishDetailDTO);
            listPublishDetailDTOList.add(listPublishDetailDTO);
        }

        return listPublishDetailDTOList;
    }
}
