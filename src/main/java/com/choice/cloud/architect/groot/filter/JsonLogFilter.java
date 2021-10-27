package com.choice.cloud.architect.groot.filter;

import com.choice.cloud.architect.groot.support.TokenInfo;
import com.choice.cloud.architect.groot.support.TokenOperation;
import com.choice.cloud.architect.groot.support.TokenUser;
import com.choice.driver.entity.LogConst;
import io.jaegertracing.internal.JaegerSpanContext;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.List;

@Order(-1001)
@Component
public class JsonLogFilter implements Filter {
    // JWT获取参数，没用jwt可以删除这一段
    @Autowired
    private TokenOperation tokenOperation;
    public static final String JWT_HEADER_NAME = "Authorization";
    public static final String JWT_HEADER_VALUE_PREFIX = "Bearer ";
    public static final String JWTAUTH_BACKEND_CLIENT = "JWTAUTH_BACKEND_CLIENT";
    public static final String JWTAUTH_CHOICE_USER = "JWTAUTH_CHOICE_USER";
    public static final String JWTAUTH_BACKEND_CLIENT_NAME = "jwtauthBackendClientName";
    // END JWT获取参数，没用jwt可以删除这一段

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // 系统公共参数
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        if (httpServletRequest.getRequestURI().contains("swagger")) {
            // TODO: 2020/4/9 临时放开swagger
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        MDC.put(LogConst.URI, httpServletRequest.getRequestURI());
        String name = ManagementFactory.getRuntimeMXBean().getName();
        if(!StringUtils.isEmpty(name)) {
            String pid = name.split("@")[0];
            MDC.put(LogConst.PID, pid);
        }
        Tracer tracer = GlobalTracer.get();
        JaegerSpanContext ctx = (JaegerSpanContext) tracer.scopeManager().active().span().context();
        String traceId = Long.toHexString(ctx.getTraceId());
        String spanId = Long.toHexString(ctx.getSpanId());
        MDC.put(LogConst.TRACE_ID, traceId);
        MDC.put(LogConst.SPAN_ID, spanId);
        // END 系统公共参数

        // 业务公共参数自行传入, 用jwt的可以沿用下面代码获取商户、用户信息。也可以自己根据自己参数自由传值
        String token = httpServletRequest.getHeader(JWT_HEADER_NAME);
        if (org.apache.commons.lang.StringUtils.startsWith(token, JWT_HEADER_VALUE_PREFIX)) {
            token = org.apache.commons.lang.StringUtils.substringAfter(token, JWT_HEADER_VALUE_PREFIX);
        }
        if (!StringUtils.isEmpty(token)) {
            TokenInfo tokenInfo = tokenOperation.convertToken(token);
            List<String> authorities = tokenInfo.getAuthorities();
            if (authorities.contains(JWTAUTH_BACKEND_CLIENT) || authorities.contains(JWTAUTH_CHOICE_USER)) {
                //属于后端服务调用
                MDC.put(JWTAUTH_BACKEND_CLIENT_NAME, tokenInfo.getUserName());
            }else{
                TokenUser tokenUser = tokenOperation.getTokenUser(token);
                if (null != tokenUser) {
                    MDC.put(LogConst.TENANT_ID, tokenUser.getTid());
                    MDC.put(LogConst.USER_ID, tokenUser.getUid());
                    MDC.put(LogConst.ACCOUNT_ID, tokenUser.getAid());
                }
            }
        }
        // END 业务公共参数

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
    }
}