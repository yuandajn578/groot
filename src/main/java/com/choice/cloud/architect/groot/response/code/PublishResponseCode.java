package com.choice.cloud.architect.groot.response.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/11 11:33
 */
@Getter
@AllArgsConstructor
public enum PublishResponseCode  implements ResponseInfo {

    JENKINS_BUILD_NOT_FOUND("26666","jenkins中不存在该构建"),
    PUBLISH_NOT_FOUND("26667","发布不存在"),
    PUBLISH_UPDATE("26668","发布更新失败"),
    CHANGE_HAS_BIND("26669","变更单已被关联，无法重复关联"),
    CHANGE_NO_BIND("26670","变更单没有此审核单关联，无法解绑"),
    BUILD_RECORD_NOT_FOUND("26671","构建记录不存在"),
    TEST_STATUS_ERROR("26672","测试状态不存在"),
    SET_TEST_STATUS_ERROR("26673","测试状态已设置，或者发布状态不合法"),
    JOB_IS_BUILDING("26674","关联Jenkins Job正在执行其他构建，请稍后重试"),
    WRONG_PUBLISH_STATUS("26675","只有待发布状态允许发布"),
    PUBLISH_AUDIT_ERROR("26676","未创建审核或审核尚未通过"),
    PUBLISH_TIME_ILLEGAL("26679","封网期间，请走故障发布流程"),
    ROLLBACK_DEPLOYING_ILLEGAL("26680","正在构建的应用不允许回滚"),
    JENKINS_RESPONSE_SLOW("26681","对应job还未建立，第一次发布自动建立，或者jenkins太忙，无响应！"),
    PUBLISH_AUDIT_NOT_FOUND("26682","还未创建审核"),
    CAN_NOT_ONLINE_PUBLISH("26683","线下Groot不能发布线上环境"),
    EXCEPT_PUBLISH_TIME_ILLEGAL("26684","发布时间不在审核预期发布时间窗口内（审核预期发布时间后8小时内可发布），此发布将会关闭"),
    DAILY_PUBLISH_TIME_ILLEGAL("26685","日常发布需提前12小时发起申请，研发自测发布需要提前3小时申请，立即发布请走非日常发布"),
    POD_PUBLISH_INFO_IS_EMPTY("26686", "Pod发布信息为空"),
    APP_NOT_DEPLOYED("26687", "当前应用在该LDC下无部署，无法扩容"),
    SCALE_UP_AT_LEAST_ONE("26688", "至少扩容一个节点"),
    PUBLISHER_EMPTY("26689", "未录入应用发布人员，请联系应用owner录入发布人员"),
    NO_PUBLISH_PERMISSION("26690", "没有发布权限，如需发布请联系应用owner，在应用页面添加对应发布人员"),
    ROLLBACK_SAME_REVISION("26691", "回滚的发布版本与当前Kubernetes的Deployment版本相同"),
    ;

    /** 状态码 */
    private final String code;

    /** 描述信息 */
    private final String desc;
}
