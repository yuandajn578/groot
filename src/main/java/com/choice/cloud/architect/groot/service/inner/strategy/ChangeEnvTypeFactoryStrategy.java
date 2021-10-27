package com.choice.cloud.architect.groot.service.inner.strategy;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 环境阶段策略工厂类
 * </p>
 *
 * @author LZ
 * @date Created in 2020/8/19 14:10
 */
public class ChangeEnvTypeFactoryStrategy {
    private static final Map<String, ChangeEnvTypeStrategy> envTypeStrategies = new HashMap<>();

    static {
        envTypeStrategies.put("test", new TestEnvTypeStrategy());
        envTypeStrategies.put("pre", new PreEnvTypeStrategy());
        envTypeStrategies.put("pro", new ProEnvTypeStrategy());
    }

    public static ChangeEnvTypeStrategy getStrategy(String type) {
        if (type == null || type.isEmpty()) {
            throw new IllegalArgumentException("type should not be empty.");
        }
        return envTypeStrategies.get(type);
    }
}
