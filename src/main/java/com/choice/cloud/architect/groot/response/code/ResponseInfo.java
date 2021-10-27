package com.choice.cloud.architect.groot.response.code;

/**
 * @classDesc:
 * @author: yuxiaopeng
 * @createTime: 2018/12/18
 * @version: v1.0.0
 * @copyright: 北京辰森
 * @email: yuxiaopeng@choicesoft.com.cn
 */
public interface ResponseInfo {

    /**
     * 获取响应Code
     * @return
     */
    String getCode();

    /**
     * 获取响应Desc
     * @return
     */
    String getDesc();
}
