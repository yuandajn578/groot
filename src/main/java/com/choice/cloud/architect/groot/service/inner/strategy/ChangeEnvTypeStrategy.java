package com.choice.cloud.architect.groot.service.inner.strategy;

/**
 * <p>
 * 应用变更单验收，环境阶段策略接口
 * </p>
 *
 * @author LZ
 * @date Created in 2020/8/19 14:02
 */
public interface ChangeEnvTypeStrategy {
    /**
     * 根据当前环境阶段，获取下次环境阶段
     * @return
     */
    String getNextState();

    /**
     * 根据当前阶段，获取下次变更单状态
     * @return
     */
    String getNextStatus();
}
