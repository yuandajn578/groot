package com.choice.cloud.architect.groot.service.inner.impl;

import com.choice.cloud.architect.groot.dao.GrResourceSpecMapper;
import com.choice.cloud.architect.groot.dto.ResourceSpecDTO;
import com.choice.cloud.architect.groot.model.GrResourceSpec;
import com.choice.cloud.architect.groot.request.CreateResourceSpecRequest;
import com.choice.cloud.architect.groot.service.inner.GrResourceSpecService;
import com.choice.driver.jwt.entity.AuthUser;
import com.google.common.collect.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhangkun
 */
@Service
public class GrResourceSpecServiceImpl extends BaseService implements GrResourceSpecService {
    @Autowired
    private GrResourceSpecMapper grResourceSpecMapper;

    @Override
    public void save(CreateResourceSpecRequest request, AuthUser authUser) {
        GrResourceSpec resourceSpec = new GrResourceSpec();

        initSaveModel(resourceSpec, authUser);
        BeanUtils.copyProperties(request, resourceSpec);

        grResourceSpecMapper.insert(resourceSpec);
    }

    @Override
    public List<ResourceSpecDTO> list() {
        List<ResourceSpecDTO> resourceSpecDTOList = Lists.newArrayList();
        List<GrResourceSpec> resourceSpecList = grResourceSpecMapper.list();

        for (GrResourceSpec resourceSpec : resourceSpecList) {
            ResourceSpecDTO resourceSpecDTO = new ResourceSpecDTO();
            BeanUtils.copyProperties(resourceSpec, resourceSpecDTO);

            resourceSpecDTOList.add(resourceSpecDTO);
        }

        return resourceSpecDTOList;
    }
}
