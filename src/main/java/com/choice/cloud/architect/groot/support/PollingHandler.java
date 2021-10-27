package com.choice.cloud.architect.groot.support;

import org.apache.http.util.Asserts;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @Description 轮询处理模板
 * @Author: zhangguoquan
 * @Date: 2020/3/18 13:32
 */
public class PollingHandler<T> {

    public T process(int failureCount, int periodSecond, Supplier<T> loop) throws InterruptedException {
        Objects.requireNonNull(loop);
        Asserts.check(periodSecond > 0, "periodSecond must be larger than zero");
        int i = 0;
        T result = loop.get();
        while (result == null && i < failureCount) {
            TimeUnit.SECONDS.sleep(periodSecond);
            result = loop.get();
            i++;
        }
        return result;
    }

    public T processCondition(int failureCount, int periodSecond, Supplier<T> loop, Supplier<Boolean> condition) throws InterruptedException {
        Objects.requireNonNull(loop);
        Asserts.check(periodSecond > 0, "periodSecond must be larger than zero");
        int i = 0;
        T result;
        do {
            TimeUnit.SECONDS.sleep(periodSecond);
            result = loop.get();
            i++;
        } while (condition.get() && i < failureCount);
        return result;
    }
}
