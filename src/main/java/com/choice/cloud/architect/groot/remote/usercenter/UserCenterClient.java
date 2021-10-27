package com.choice.cloud.architect.groot.remote.usercenter;

import java.util.List;

import com.choice.cloud.architect.groot.remote.DefaultClientFallbackFactory;
import com.choice.cloud.architect.groot.response.ResponseData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author zhangkun
 */
@FeignClient(value = "USERCENTER",fallbackFactory = DefaultClientFallbackFactory.class)
public interface UserCenterClient {
    @PostMapping("/api/inner/user/queryUserById")
    ResponseData<UserDTO> queryUserById(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String token, @RequestParam String id);

    @PostMapping("/api/inner/user/queryUserListByIds")
    ResponseData<List<UserDTO>> queryUserListByIds(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String token, @RequestBody List<String> ids);
}
