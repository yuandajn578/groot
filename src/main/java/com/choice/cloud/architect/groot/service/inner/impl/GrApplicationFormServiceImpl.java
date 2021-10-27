package com.choice.cloud.architect.groot.service.inner.impl;

import com.choice.cloud.architect.groot.dao.GrApplicationFormMapper;
import com.choice.cloud.architect.groot.dto.ListAppPublishApplyDTO;
import com.choice.cloud.architect.groot.exception.ServiceException;
import com.choice.cloud.architect.groot.model.GrApplicationForm;
import com.choice.cloud.architect.groot.model.GrFormChangeEnv;
import com.choice.cloud.architect.groot.option.WebPage;
import com.choice.cloud.architect.groot.request.CreateAppPublishApplyRequest;
import com.choice.cloud.architect.groot.request.ListAppPublishApplyRequest;
import com.choice.cloud.architect.groot.service.inner.GrApplicationFormService;
import com.choice.cloud.architect.groot.service.inner.GrChangeService;
import com.choice.cloud.architect.groot.service.inner.GrEnvService;
import com.choice.cloud.architect.groot.service.inner.GrFormChangeEnvService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhangkun
 */
@Service
public class GrApplicationFormServiceImpl implements GrApplicationFormService {
    @Autowired
    private GrApplicationFormMapper grApplicationFormMapper;

    @Autowired
    private GrFormChangeEnvService grFormChangeEnvService;

    @Autowired
    private GrChangeService grChangeService;

    @Autowired
    private GrEnvService grEnvService;

    @Override
    public void createAppPublishApply(CreateAppPublishApplyRequest createAppPublishApplyRequest) {
        // 0.数据校验
        if (!grChangeService.existsChange(createAppPublishApplyRequest.getChangeId())) {
            throw new ServiceException("指定应用变更单不存在");
        }

        if (!grEnvService.existsEnv(createAppPublishApplyRequest.getEnvId())) {
            throw new ServiceException("指定应用环境不存在");
        }

        // 1.保存应用构建申请单
        GrApplicationForm grApplicationForm = new GrApplicationForm();
        BeanUtils.copyProperties(createAppPublishApplyRequest, grApplicationForm);
        grApplicationFormMapper.insert(grApplicationForm);

        // 2.保存应用构建申请单 - 应用变更单 - 应用环境关系
        GrFormChangeEnv grFormChangeEnv = new GrFormChangeEnv();
        grFormChangeEnv.setFormId(grApplicationForm.getOid());
        grFormChangeEnv.setChangeId(createAppPublishApplyRequest.getChangeId());
        grFormChangeEnv.setEnvId(createAppPublishApplyRequest.getEnvId());

        grFormChangeEnvService.save(grFormChangeEnv);
    }

    @Override
    public WebPage<List<ListAppPublishApplyDTO>> list(ListAppPublishApplyRequest listAppPublishApplyRequest) {
        Integer pageNum = listAppPublishApplyRequest.getPageNum();
        Integer pageSize = listAppPublishApplyRequest.getPageSize();
        List<ListAppPublishApplyDTO> appPublishApplyDTOList = Lists.newArrayList();
        PageHelper.startPage(pageNum, pageSize);
        List<GrApplicationForm> grApplicationFormList = grApplicationFormMapper.listWithPage();

        if (CollectionUtils.isEmpty(grApplicationFormList)) {
            return new WebPage<>(pageNum, pageSize, 0, null);
        }

        for (GrApplicationForm grApplicationForm : grApplicationFormList) {
            ListAppPublishApplyDTO appPublishApplyDTO = new ListAppPublishApplyDTO();

            BeanUtils.copyProperties(grApplicationForm, appPublishApplyDTO);
            appPublishApplyDTOList.add(appPublishApplyDTO);
        }

        PageInfo<GrApplicationForm> info = new PageInfo<>(grApplicationFormList);
        return new WebPage<>(pageNum, pageSize, info.getTotal(), appPublishApplyDTOList);
    }
}
