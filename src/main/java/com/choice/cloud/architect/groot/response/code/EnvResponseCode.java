package com.choice.cloud.architect.groot.response.code;

/**
 * <p>
 * 环境管理相关响应code
 * </p>
 *
 * @author zhangkun
 */
public enum EnvResponseCode implements ResponseInfo {
    /**
     *环境管理相关响应code
     */
    EXISTED_GRAY_CONFIG_NOT_DELETE("50000", "存在灰度配置正在使用该环境，不允许删除"),
    EXISTED_GRAY_CONFIG_NOT_FROZEN("50001", "存在灰度配置正在使用该环境，不允许停用"),
    EXISTED_ACTIVE_POD_NOT_DELETE("50002", "该环境下存在正在运行的容器，不允许删除"),
    SERVICE_GROUP_NOT_EXIST_DEPLOYMENT("50004", "当前应用在该服务分组下无部署"),
    SERVICE_GROUP_NAME_SENSITIVE("50005", "不能使用环境相关的敏感名称"),
    K8S_NAMESPACE_DELETE_FAILURE("50006", "K8S namespace删除失败，请联系运维处理"),
    NOT_ALLOW_DEL_ENV("50007", "该环境不允许删除"),
    CONTAINER_NOT_EXIST_POD("50008", "容器下不存在POD"),
    REDEPLOY_ONLINE("50009", "此环境环境暂不开放重新部署/重启功能"),
    REPLICAS_MORE_THAN_TWO("50010", "扩容两个以上需要运维同学手动操作"),
    ENV_CODE_ALREADY_EXIST("50011", "环境编码已存在"),
    ENV_RELATION_NOT_FOUND("50012", "关联环境不存在"),
    ROLLBACK_FAILURE("50013", "回滚失败，请联系运维同学查看Kubernetes中回滚版本是否还存在"),
    ;

    /**
     * 状态码
     */
    private final String code;
    /**
     * 描述信息
     */
    private final String desc;

    EnvResponseCode(String code, String desc) {
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
