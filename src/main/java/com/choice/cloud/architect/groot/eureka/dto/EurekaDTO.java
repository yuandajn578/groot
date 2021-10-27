package com.choice.cloud.architect.groot.eureka.dto;

import lombok.Data;

/**
 * @ClassName EurekaDTO
 * @Description TODO
 * @Author Guangshan Wang
 * @Date 2020/5/15/015 14:41
 */
@Data
public class EurekaDTO {

    String user;
    String password;
    String url;

    public EurekaDTO() {}

    public EurekaDTO(String user, String password, String url) {
        this.user = user;
        this.password = password;
        this.url = url;
    }
}
