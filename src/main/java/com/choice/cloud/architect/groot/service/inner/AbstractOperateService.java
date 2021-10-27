package com.choice.cloud.architect.groot.service.inner;


import com.choice.cloud.architect.groot.dao.GrChangeMapper;
import com.choice.cloud.architect.groot.enums.OperateEnum;
import com.choice.cloud.architect.groot.exception.ServiceException;
import com.choice.cloud.architect.groot.model.GrChange;
import org.springframework.beans.factory.annotation.Autowired;


public abstract class AbstractOperateService<Resp> implements IOperateService<Resp>{

    protected GrChangeMapper grChangeMapper;
    @Autowired
    public void setGrChangeMapper(GrChangeMapper grChangeMapper) {
        this.grChangeMapper = grChangeMapper;
    }


    @Override
    public Resp operate(GrChange req) throws Exception {
        GrChange oldChange = grChangeMapper.selectById(req.getOid());
        if(null == oldChange){
            throw new ServiceException("变更单不存在");
        }
        canDoOperate(OperateEnum.status2Enum(oldChange.getStatus()));
        GrChange newChange = new GrChange();
        newChange.setOid(req.getOid());
        setStatus(newChange);

        doOtherOperate(newChange);

        if(null == newChange.getStatus()){
            throw new ServiceException("变更状态不能为空");
        }
        grChangeMapper.updateDynamic(newChange);
        return null;
    }

    protected abstract void setStatus(GrChange grChange);


    protected abstract void doOtherOperate(GrChange grChange) throws Exception;
}
