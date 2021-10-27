package com.choice.cloud.architect.groot.filter;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.choice.cloud.architect.groot.properties.WhiteListProperties;
import com.choice.cloud.architect.groot.response.ResponseData;
import com.choice.cloud.architect.groot.response.code.SysResponseCode;
import com.choice.driver.jwt.core.JwtHandler;
import com.choice.driver.jwt.entity.AuthUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @classDesc:
 * @author: yuxiaopeng
 * @createTime: 2020-02-22
 * @version: v1.0.0
 * @email: yuxiaopeng@choicesoft.com.cn
 */
@Slf4j
//@Configuration
public class AuthFilter implements Filter {

    private WhiteListProperties whiteListProperties;

    private JwtHandler jwtHandler;

    public AuthFilter(WhiteListProperties whiteListProperties, JwtHandler jwtHandler) {
        this.whiteListProperties = whiteListProperties;
        this.jwtHandler = jwtHandler;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String servletPath = request.getServletPath();
        List<String> whiteList= whiteListProperties.getList();

        if(CollectionUtils.isNotEmpty(whiteList) && whiteList.contains(servletPath)){
            log.debug("[AuthFilter]白名单放行,Path:{}",servletPath);
            filterChain.doFilter(request,response);
            return;
        }
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        try{
            AuthUser authUser = jwtHandler.verifyJwt(header);
            log.debug("[AuthFilter]Jwt校验成功,用户信息:{}",authUser);
        }catch (JWTVerificationException e){
            log.debug("[AuthFilter]Jwt校验失败,Path:{},错误信息:{}",servletPath,e.getMessage());
            ResponseData responseData = ResponseData.createByError(SysResponseCode.TOKEN_VALIDATE_ERROR);
            String result = JSON.toJSONString(responseData);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
            response.setHeader(HttpHeaders.CONTENT_LENGTH,String.valueOf(result.getBytes(Charset.defaultCharset()).length));
            response.getWriter().println(result);
            return;
        }
        filterChain.doFilter(request,response);
    }

    @Override
    public void destroy() {

    }
}
