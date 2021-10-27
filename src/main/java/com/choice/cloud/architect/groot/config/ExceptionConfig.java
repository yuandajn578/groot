package com.choice.cloud.architect.groot.config;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.choice.cloud.architect.groot.exception.ServiceException;
import com.choice.cloud.architect.groot.response.ResponseData;
import com.choice.cloud.architect.groot.response.code.ResponseInfo;
import com.choice.cloud.architect.groot.response.code.SysResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yuxiaopeng
 * @version v1.0
 * @description
 * @createTime 2018-10-16 10:44
 * @email
 */
@Slf4j
@RestControllerAdvice
public class ExceptionConfig {

    /**
     * ServiceException 异常处理
     * @param serviceException
     * @return
     */
    @ExceptionHandler(value = ServiceException.class)
    public ResponseData serviceExceptionHandler(ServiceException serviceException) {
        log.warn("[ServiceException Handler]Exception:{}", ExceptionUtils.getStackTrace(serviceException));
        ResponseInfo responseInfo = serviceException.getResponseInfo();
        if(null != responseInfo){
           return ResponseData.createByError(responseInfo);
        }
        return ResponseData.createByError(SysResponseCode.ERROR,serviceException.getMessage());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(value = JWTVerificationException.class)
    public ResponseData jwtVerificationExceptionHandler(JWTVerificationException jwtVerificationException){
        log.warn("[JWTVerificationException Handler]Exception:{}", ExceptionUtils.getStackTrace(jwtVerificationException));
        return ResponseData.createByError(SysResponseCode.TOKEN_VALIDATE_ERROR,jwtVerificationException.getMessage());
    }

    /**
     * Exception 异常处理
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    public ResponseData exceptionHandler(Exception e) {
        log.error("[Exception Handler](errMsg)Exception:{}", ExceptionUtils.getStackTrace(e));
        return ResponseData.createByError(SysResponseCode.ERROR,e.getMessage());
    }

    /**
     * 参数校验异常处理
     * @param e
     * @return
     */
    @ExceptionHandler(value = {
            MethodArgumentNotValidException.class,
            BindException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class })
    public ResponseData illegalParamErrorHandler(Exception e) {
        List<String> errorParams = new ArrayList<>();
        String errorMessage = "";
        if(e instanceof MethodArgumentNotValidException){
            MethodArgumentNotValidException exception = (MethodArgumentNotValidException) e;
            exception.getBindingResult().getFieldErrors().forEach(i->errorParams.add(i.getField()));
            FieldError fieldError = exception.getBindingResult().getFieldError();
            errorMessage = fieldError != null? fieldError.getDefaultMessage() : "";
        }else if(e instanceof BindException){
            BindException exception = (BindException) e;
            exception.getFieldErrors().forEach(i->errorParams.add(i.getField()));
            FieldError fieldError = exception.getFieldError();
            errorMessage = fieldError != null? fieldError.getDefaultMessage() : "";
        }else if(e instanceof MissingServletRequestParameterException){
            MissingServletRequestParameterException exception = (MissingServletRequestParameterException) e;
            errorParams.add(exception.getParameterName());
        }else if(e instanceof MethodArgumentTypeMismatchException){
            MethodArgumentTypeMismatchException exception = (MethodArgumentTypeMismatchException) e;
            errorParams.add(exception.getName());
        }
        log.warn("[IllegalParamErrorHandler ]ErrorMsg:{},Exception:{}",errorMessage,ExceptionUtils.getStackTrace(e));
        return ResponseData.createByError(SysResponseCode.PARAM_ILLEGAL,errorParams);
    }
}
