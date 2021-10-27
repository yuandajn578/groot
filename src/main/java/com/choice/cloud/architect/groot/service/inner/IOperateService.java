package com.choice.cloud.architect.groot.service.inner;

import com.choice.cloud.architect.groot.enums.OperateEnum;
import com.choice.cloud.architect.groot.model.GrChange;

public interface IOperateService<Resp> {

    OperateEnum operateType();

    void canDoOperate(OperateEnum operateEnum);

    Resp operate(GrChange req) throws Exception;

}
