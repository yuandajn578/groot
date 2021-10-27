package com.choice.cloud.architect.groot.util;

import com.choice.cloud.architect.groot.exception.ServiceException;
import com.choice.cloud.architect.groot.remote.usercenter.UserCenterClient;
import com.choice.cloud.architect.groot.remote.usercenter.UserDTO;
import com.choice.cloud.architect.groot.response.ResponseData;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhangkun
 */
public class UserHolder {
    public static String getUserName(String uid) {
        HttpServletRequest currentRequest = CommonUtil.getCurrentRequest();

        if (null == currentRequest) {
            return null;
        }

        String jwtHeader = currentRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (null == jwtHeader) {
            // TODO 颁发一个固定的token来鉴权
        }

        UserCenterClient userCenterClient = AppUtil.getBean(UserCenterClient.class);
        ResponseData<UserDTO> userDTOResponseData = userCenterClient.queryUserById(jwtHeader, uid);
        UserDTO userDTO = userDTOResponseData.getData();

        if(null == userDTO){
            throw new ServiceException("user info is null");
        }
        return userDTO.getRealName();
    }
}
