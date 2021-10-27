package com.choice.cloud.architect.groot.remote;

import com.choice.cloud.architect.groot.response.ResponseData;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DefaultClientFallbackFactory implements FallbackFactory<ResponseData> {

    @Override
    public ResponseData create(Throwable throwable) {
        log.error("---->>>> feign remote error msg: {} stack: {}", throwable.getMessage(),throwable);
        return ResponseData.createByError();
    }
}
