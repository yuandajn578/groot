package com.choice.cloud.architect.groot.exception;

import com.choice.cloud.architect.groot.response.code.ResponseInfo;

/**
 * @author yuxiaopeng
 * @version v1.0
 * @description
 * @createTime 2018-10-16 10:04
 * @email
 */
public class ServiceException extends RuntimeException {

    private ResponseInfo responseInfo;

    public ServiceException(String message){
        super(message);
    }

    public ServiceException(ResponseInfo responseInfo){
        super(responseInfo.getDesc());
        this.responseInfo = responseInfo;
    }

    public ResponseInfo getResponseInfo() {
        return responseInfo;
    }

    public ServiceException setResponseInfo(ResponseInfo responseInfo) {
        this.responseInfo = responseInfo;
        return this;
    }
}
