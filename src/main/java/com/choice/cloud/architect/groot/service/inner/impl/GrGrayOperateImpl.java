package com.choice.cloud.architect.groot.service.inner.impl;

import com.choice.cloud.architect.groot.enums.OperateEnum;
import com.choice.cloud.architect.groot.exception.ServiceException;
import com.choice.cloud.architect.groot.model.GrChange;
import com.choice.cloud.architect.groot.service.inner.AbstractOperateService;
import org.springframework.stereotype.Service;

@Service
public class GrGrayOperateImpl extends AbstractOperateService<Object> {

    @Override
    protected void setStatus(GrChange grChange) {
        grChange.setStatus(OperateEnum.PROCESS_GRAY.getStatus());
    }

    @Override
    protected void doOtherOperate(GrChange grChange) throws Exception {
        //TODO
    }

    @Override
    public OperateEnum operateType() {
        return OperateEnum.PROCESS_GRAY;
    }

    @Override
    public void canDoOperate(OperateEnum operateEnum) {
        if(!operateEnum.name().equals(OperateEnum.PROCESS_DOTEST)){
            throw new ServiceException("当前状态不可灰度");
        }
    }
}
