package com.choice.cloud.architect.groot.response.code;

/**
 * @classDesc: 状态码
 * @version: v1.0
 * @copyright: 北京辰森
 */
public enum GitlabResponseCode implements ResponseInfo {
    NO_GIT_REP("26670","没有查到git仓库"),
    MERGE_ERROR("26671","合并分支失败,请尝试手动合并代码"),
    CREATE_BRANCH_ERROR("26672","创建分支失败"),
    MERGE_ERROR_CANNOT_BE_MERGED("26673","合并分支失败，请确认是否存在冲突"),
    MERGE_ERROR_NOT_FIND_MERGED("26674","合并分支失败，没有找到合并请求"),

    ;


    /**
     * 状态码
     */
    private final String code;
    /**
     * 描述信息
     */
    private final String desc;

    GitlabResponseCode(String code, String desc) {
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
