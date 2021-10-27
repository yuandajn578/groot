package com.choice.cloud.architect.groot;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhangkun
 */
public class IdcSelectorTest {
    private AtomicInteger atomicInteger = new AtomicInteger();
    List<String> idcList = Lists.newArrayList("idc02", "idc03");

    @Test
    public void testSelect() {
        for (int i = 0; i < 10; i++) {
            System.out.println(idcList.get(atomicInteger.getAndIncrement() % idcList.size()));
        }
    }
}
