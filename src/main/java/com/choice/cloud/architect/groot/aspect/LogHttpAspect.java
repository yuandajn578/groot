package com.choice.cloud.architect.groot.aspect;

import com.aliyun.openservices.shade.org.apache.commons.lang3.exception.ExceptionUtils;
import com.choice.cloud.architect.groot.response.ResponseData;
import com.choice.driver.entity.DigestLog;
import com.choice.driver.enums.LogErrorCodeEnums;
import com.choice.driver.enums.LogOriginEnum;
import com.google.common.collect.Lists;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author lanboo
 * @date 2020/4/3 18:02
 */
@Aspect
@Component
public class LogHttpAspect {
    private static final String PONG = "pong";

    private static final List<String> SUCCESS_CODE_LIST = Lists.newArrayList("200", "10000");

    public static final Logger LOGGER = LoggerFactory.getLogger(LogHttpAspect.class);

    private static final String POINCUT = "";

    @Around("execution(public * com.choice.cloud..*.controller..*.*(..))")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String requestURI = request.getRequestURI();
        String origin = LogOriginEnum.IN.getValue();
        String errorCode = null, status = "Y", errMsg = null;

        try {
            Object result = joinPoint.proceed();
            if (null == result) {
                return null;
            }

            if (PONG.equals(result)) {
                status = "Y";
            }

            if (result.getClass().isAssignableFrom(ResponseData.class)) {
                ResponseData responseData = (ResponseData) result;
                if (SUCCESS_CODE_LIST.contains(responseData.getCode())) {
                    status = "Y";
                } else {
                    errorCode = responseData.getCode();
                    errMsg = responseData.getMsg();
                    status = "N";
                }
            } else if (result.getClass().isAssignableFrom(String.class)) {

            } else {
                LOGGER.warn("unknown http response type, {}", result);
            }

            long elapsedTime = System.currentTimeMillis() - startTime;
            DigestLog.digestLog(requestURI, origin, errorCode, status, elapsedTime, errMsg);
            return result;
        } catch (Throwable thr) {
            errorCode = LogErrorCodeEnums.SYSTEM_ERROR.getValue();
            status = "N";
            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;

            DigestLog.digestLog(requestURI, origin, errorCode, status, elapsedTime, thr.getMessage());
            LOGGER.error("handle http request, path = [{}] occur exception, stack trace = {}", requestURI, ExceptionUtils.getStackTrace(thr));

            throw thr;
        }
    }
}
