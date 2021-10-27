package com.choice.cloud.architect.groot.service.inner.strategy;

/**
 * <p>
 * 测试阶段策略类
 * </p>
 *
 * @author LZ
 * @date Created in 2020/8/19 14:08
 */
public class TestEnvTypeStrategy implements ChangeEnvTypeStrategy {

    /**
     * 根据当前环境阶段，获取下次环境阶段
     *
     * @return
     */
    @Override
    public String getNextState() {
        return null;
    }

    /**
     * 根据当前阶段，获取下次变更单状态
     *
     * @return
     */
    @Override
    public String getNextStatus() {
        return null;
    }
}
