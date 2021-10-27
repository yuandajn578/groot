package com.choice.cloud.architect.groot.dto.apollo;

import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * <p>
 * Apollo 创建用户请求体
 * </p>
 *
 * @author LZ
 * @date Created in 2020/5/25 20:42
 */
@Data
public class ApolloCreateUserRequestDTO {
    /**
     * username : zhangsan
     * password : 123456
     * email : zhangsan@qq.com
     */
    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotBlank(message = "密码不能为空")
    private String password;
    @NotBlank(message = "邮箱不能为空")
    private String email;
}
