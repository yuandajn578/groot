package com.choice.cloud.architect.groot.service.inner.impl;

import com.choice.cloud.architect.groot.enums.OperateEnum;
import com.choice.cloud.architect.groot.exception.ServiceException;
import com.choice.cloud.architect.groot.model.GrChange;
import com.choice.cloud.architect.groot.service.inner.AbstractOperateService;
import org.springframework.stereotype.Service;

@Service
public class GrTestOperateImpl extends AbstractOperateService<Object> {

    @Override
    public OperateEnum operateType() {
        return OperateEnum.PROCESS_DOTEST;
    }

    @Override
    public void canDoOperate(OperateEnum operateEnum) {
        if(!operateEnum.name().equals(OperateEnum.PROCESS_DEVELOP)){
            throw new ServiceException("当前状态不可提测");
        }
    }

    @Override
    protected void setStatus(GrChange grChange) {
        grChange.setStatus(OperateEnum.PROCESS_DOTEST.getStatus());
    }

    @Override
    protected void doOtherOperate(GrChange grChange) throws Exception {
        //TODO
    }
}
