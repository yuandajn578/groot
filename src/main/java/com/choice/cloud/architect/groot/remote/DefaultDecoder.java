package com.choice.cloud.architect.groot.remote;

import com.choice.cloud.architect.groot.exception.ServiceException;
import com.choice.cloud.architect.groot.response.ResponseData;
import feign.FeignException;
import feign.Request;
import feign.Response;
import feign.codec.DecodeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.io.IOException;
import java.lang.reflect.Type;

//@Component
@Slf4j
public class DefaultDecoder extends SpringDecoder {


    public DefaultDecoder(ObjectFactory<HttpMessageConverters> messageConverters) {
        super(messageConverters);
    }

    @Override
    public Object decode(Response response, Type type) throws IOException, FeignException {
        Request request = response.request();

        boolean isResponseData = ResponseData.class.isAssignableFrom(type.getClass());
        // 兼容MessageBody返回值
        if(isResponseData) {
            Object obj = super.decode(response, type);
            log.info("远程调用接口返回值：url：{}，body:{}",request.url(),obj);
            return obj;
        } else {
                type = ParameterizedTypeImpl.make(ResponseData.class, new Type[]{type}, null);
                ResponseData body = (ResponseData) super.decode(response, type);
                log.info("远程调用接口返回值：url：{}，body:{}",request.url(),body);
                if(body==null) {
                    log.error("远程调用接口 MessageBody==null！");
                    return null;
                }
                if(!body.isSuccess()) {
                    log.error("远程调用接口出现异常！url={},errMsg={}",request.url(),body.getMsg());
                    throw new ServiceException(body.getMsg());
                }
                return body.getData();
        }
    }

}
