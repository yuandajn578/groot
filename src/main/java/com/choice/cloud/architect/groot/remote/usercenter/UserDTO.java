package com.choice.cloud.architect.groot.remote.usercenter;

import lombok.Data;
import lombok.ToString;

/**
 * @author zhangkun
 */
@Data
@ToString
public class UserDTO {
    /**
     * 员工id
     */
    private String id;

    /**
     * 员工姓名
     */
    private String realName;

    /**
     * 手机
     */
    private String mobile;
}
