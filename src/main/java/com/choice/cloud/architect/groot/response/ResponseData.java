package com.choice.cloud.architect.groot.response;

import com.choice.cloud.architect.groot.response.code.ResponseInfo;
import com.choice.cloud.architect.groot.response.code.SysResponseCode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author yuxiaopeng
 * @version v1.0
 * @description
 * @createTime 2018-10-22 10:57
 * @email
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class ResponseData<T> {

    private String code;

    private String msg;

    private T data;

    private Page page;

    public ResponseData(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResponseData(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ResponseData(String code, String msg, T data, Page page) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.page = page;
    }

    public ResponseData(ResponseInfo responseInfo){
        this(responseInfo,null,null);
    }

    public ResponseData(ResponseInfo responseInfo, T data){
        this(responseInfo,data,null);
    }

    public ResponseData(ResponseInfo responseInfo, T data, Page page){
        this.code = responseInfo.getCode();
        this.msg = responseInfo.getDesc();
        this.data = data;
        this.page = page;
    }

    public static <T> ResponseData<T> createBySuccess() {
        return new ResponseData<T>(SysResponseCode.SUCCESS);
    }

    public static <T> ResponseData<T> createBySuccess(T data) {
        return new ResponseData<T>(SysResponseCode.SUCCESS,data);
    }

    public static <T> ResponseData<T> createBySuccess(Page page, T data) {
        return new ResponseData<T>(SysResponseCode.SUCCESS, data, page);
    }

    public static <T> ResponseData<T> createByError() {
        return new ResponseData<T>(SysResponseCode.ERROR);
    }

    public static <T> ResponseData<T> createByError(ResponseInfo responseInfo) {
        return new ResponseData<T>(responseInfo);
    }

    public static <T> ResponseData<T> createByError(ResponseInfo responseInfo,T data) {
        return new ResponseData<T>(responseInfo,data);
    }

    public static <T> ResponseData<T> createByError(String errMsg) {
        return new ResponseData<T>(SysResponseCode.ERROR.getCode(),errMsg);
    }

    @JsonIgnore
    public boolean isSuccess() {
        return this.code.equalsIgnoreCase("10000") || this.msg.equalsIgnoreCase("SUCCESS");
    }

}
