package com.choice.cloud.architect.groot.aspect;

import com.choice.driver.entity.DigestLog;
import com.choice.driver.enums.LogErrorCodeEnums;
import com.choice.driver.enums.LogOriginEnum;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author lanboo
 * @date 2020/4/3 18:24
 */
@Aspect
@Component
public class LogDBAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogDBAspect.class);

    @Around("execution(public * com.choice.cloud..*.dao..*.*(..))")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Signature signature = joinPoint.getSignature();
        String origin = LogOriginEnum.DB.getValue();
        String  status = "Y";
        try {
            Object result = joinPoint.proceed();

            long elapsedTime = System.currentTimeMillis() - startTime;

            DigestLog.digestLog(signature.toShortString(), origin, null, status, elapsedTime, null);

            return result;
        } catch (Throwable e) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            DigestLog.digestLog(signature.toShortString(), origin, LogErrorCodeEnums.DB_ERROR.getValue(), status, elapsedTime, null);
            LOGGER.error("handle db operation, method = [{}], occur exception, stack trace = {}", signature.toShortString(), ExceptionUtils.getStackTrace(e));
            throw e;
        }
    }
}
