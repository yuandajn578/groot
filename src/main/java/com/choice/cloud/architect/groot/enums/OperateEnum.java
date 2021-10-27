package com.choice.cloud.architect.groot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OperateEnum {

    PROCESS_CREATE_CHANGE(-1,"创建变更"),
    PROCESS_DEVELOP(0,"开发构建"),
    PROCESS_DOTEST(1,"提测"),
    PROCESS_GRAY(2,"灰度发布"),
    PROCESS_PUBLISH_CHECK(-2,"线上发布审核"),
    PROCESS_PUBLISH(3,"发布"),
    PROCESS_CLOSE(4,"关闭");

    private Integer status;

    private String msg;

    public static OperateEnum status2Enum(Integer status){
        for (OperateEnum operateEnum:OperateEnum.values()) {
            if(operateEnum.getStatus().equals(status)){
                return operateEnum;
            }
        }
        return null;
    }

}
