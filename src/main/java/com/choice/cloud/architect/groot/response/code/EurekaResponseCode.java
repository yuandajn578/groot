package com.choice.cloud.architect.groot.response.code;

/**
 * @ClassName EurekaResponseCode
 * @Description eureka操作响应码
 * @Author Guangshan Wang
 * @Date 2020/7/14/014 14:13
 */
public enum EurekaResponseCode implements ResponseInfo {

    NO_FOUND_INSTANCE("400001", "没有找到对应的实例信息"),
    ;

    /**
     * 状态码
     */
    private final String code;
    /**
     * 描述信息
     */
    private final String desc;

    EurekaResponseCode(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}
