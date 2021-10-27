package com.choice.cloud.architect.groot;

import com.choice.cloud.architect.groot.support.PodNameParser;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author zhangkun
 */
public class PodNameParserTest {
    @Test
    public void testParse(){
        String podName = "sms-platform-idc01-ldc01-stable-100-0";
        PodNameParser podNameParser = new PodNameParser(podName);

        Assert.assertEquals("sms-platform", podNameParser.getAppName());
        Assert.assertEquals("idc01", podNameParser.getIdc());
        Assert.assertEquals("ldc01", podNameParser.getLdc());
        Assert.assertEquals("stable", podNameParser.getServiceGroup());
        Assert.assertEquals("100", podNameParser.getNumFlag());
    }
}
