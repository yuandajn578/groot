package com.choice.cloud.architect.groot.service.inner;

/**
 * @author zhangkun
 */
public interface IdcSelector {
    /**
     * 进行idc选择
     *
     * @return 被选择的idc
     */
    String select();
}
