package com.choice.cloud.architect.groot.util;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.choice.cloud.architect.groot.remote.usercenter.UserCenterClient;
import com.choice.cloud.architect.groot.remote.usercenter.UserDTO;
import com.choice.cloud.architect.groot.response.ResponseData;
import com.choice.driver.jwt.core.JwtHandler;
import com.choice.driver.jwt.entity.AuthUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @classDesc:
 * @author: yuxiaopeng
 * @createTime: 2020-02-22
 * @version: v1.0.0
 * @email: yuxiaopeng@choicesoft.com.cn
 */
@Slf4j
public class CommonUtil {

    /**
     * 获取真实ip地址
     *
     * @param request HttpServletRequest
     * @return
     * @author Evans
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.length() > 15 && ip.contains(",")) {
            ip = ip.substring(0, ip.indexOf(","));
        }
        return ip;
    }

    public static HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (null == attributes) {
            return null;
        }
        return attributes.getRequest();
    }

    public static HttpServletResponse getCurrentResponse() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (null == attributes) {
            return null;
        }
        return attributes.getResponse();
    }

    public static String getUUId() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }

    public static String getUUIdUseMongo() {
        return new ObjectId().toHexString();
    }

    public static String processStringLength(String original) {
        if (null == original) {
            return null;
        }
        if (original.length() > 4000) {
            return original.substring(0, 4000);
        }
        return original;
    }

    public static String processStringLength(String original, int length) {
        if (null == original) {
            return null;
        }
        if (length <= 0) {
            return processStringLength(original);
        }
        if (original.length() > length) {
            return original.substring(0, length);
        }
        return original;
    }

    public static String currentUserId() {
        JwtHandler jwtHandler = AppUtil.getBean(JwtHandler.class);

        HttpServletRequest currentRequest = getCurrentRequest();
        if (null == currentRequest) {
            return "-1";
        }

        String jwtHeader = currentRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isBlank(jwtHeader)) {
            return "-1";
        }
        AuthUser authUser = null;
        try {
            authUser = jwtHandler.extractJwt(jwtHeader);
        } catch (JWTDecodeException e) {
            log.debug("[AuthUserArgumentResolver] Extract Jwt To Auth User Failed,Exception Message:{},JwtHeader:{}", e.getMessage(), jwtHeader);
        }

        if (null == authUser) {
            return "-1";
        }

        String uid = authUser.getUid();
        UserCenterClient userCenterClient = AppUtil.getBean(UserCenterClient.class);
        ResponseData<UserDTO> userDTOResponseData = userCenterClient.queryUserById(jwtHeader, uid);
        UserDTO userDTO = userDTOResponseData.getData();

        return userDTO.getRealName();
    }

    public static String currentRequestToken() {
        HttpServletRequest currentRequest = getCurrentRequest();
        if (null == currentRequest) {
            return null;
        }

        String jwtHeader = currentRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isBlank(jwtHeader)) {
            return null;
        }

        return jwtHeader;
    }
}
