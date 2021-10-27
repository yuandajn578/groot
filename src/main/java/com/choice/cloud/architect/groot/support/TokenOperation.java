package com.choice.cloud.architect.groot.support;

import com.choice.cloud.architect.groot.exception.ServiceException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author lanboo
 * @date 2020/4/3 17:40
 */
@Component
public class TokenOperation {

    private String TOKEN_RESOLVE_ERROR = "token解析异常";
    @Autowired
    private ObjectMapper objectMapper;
    /**
     * 将token信息转化为 TokenInfo 结构
     * @param token
     * @return
     */
    public TokenInfo convertToken(String token){
        TokenInfo tokenInfo = null;
        try {
            Jwt jwt = JwtHelper.decode(token);
            String claims = jwt.getClaims();
            tokenInfo = objectMapper.readValue(claims, TokenInfo.class);
        } catch (IOException e) {
            ExceptionUtils.getStackTrace(e);
            throw new ServiceException(TOKEN_RESOLVE_ERROR);
        } catch (Exception e) {
            ExceptionUtils.getStackTrace(e);
            throw new ServiceException(TOKEN_RESOLVE_ERROR);
        }
        return tokenInfo;
    }

    public TokenUser getTokenUser(String token){
        TokenInfo tokenInfo = convertToken(token);
        TokenUser tokenUser = null;
        try {
            tokenUser = objectMapper.readValue(tokenInfo.getUserName(), TokenUser.class);
        } catch (IOException e) {
            ExceptionUtils.getStackTrace(e);
            throw new ServiceException(TOKEN_RESOLVE_ERROR);
        }
        return tokenUser;
    }

}
