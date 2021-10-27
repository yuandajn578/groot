package com.choice.cloud.architect.groot.advice;

import com.choice.cloud.architect.groot.exception.ServiceException;
import com.choice.cloud.architect.groot.response.ResponseData;
import com.choice.cloud.architect.groot.response.code.SysResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobleExceptionAdvice {

    /**
     * 全局异常
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    public ResponseData exceptionHandler(Exception e) {
        log.error("---->>>> 服务器异常 errMsg {} stack:{}",e.getMessage(),e);
        return ResponseData.createByError("服务器异常，请联系相关人员");
    }

    /**
     * 业务提倡
     * @param e
     * @return
     */
    @ExceptionHandler(value = ServiceException.class)
    public ResponseData serviceExceptionHandler(ServiceException e) {
        if (e.getResponseInfo() == null) {
            log.info("---->>>> 业务异常 errMsg:{}", e.getMessage());
            return ResponseData.createByError(e.getMessage());
        } else {
            log.info("---->>>> 业务异常 errCode {} errMsg:{}",e.getResponseInfo().getCode(),e.getResponseInfo().getDesc());
            return ResponseData.createByError(e.getResponseInfo());
        }
    }

    //处理Get请求中 使用@Valid 验证路径中请求实体校验失败后抛出的异常
    @ExceptionHandler(BindException.class)
    public ResponseData bindExceptionHandler(BindException e) {
        String message = e.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining());
        log.info("---->>>> 请求参数不合法 {}", message);
        return ResponseData.createByError(message);
    }

    //处理请求参数格式错误 @RequestParam上validate失败后抛出的异常是javax.validation.ConstraintViolationException
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseData constraintViolationExceptionHandler(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.joining());
        log.info("---->>>> 请求参数不合法 {}", message);
        return ResponseData.createByError(message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseData httpMessageNotReadableExceptionHandler(HttpMessageNotReadableException e){
        log.info("---->>>> 请求参数不合法 stack {}", e);
        return ResponseData.createByError(e.getMessage());
    }

    //处理请求参数格式错误 @RequestBody上validate失败后抛出的异常是MethodArgumentNotValidException异常。
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseData methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining());
        log.info("---->>>> 请求参数不合法 {}", message);
        return ResponseData.createByError(message);
    }
}
