package com.choice.cloud.architect.groot;/**
 * @description
 * @author seal
 * @createTime 2020-06-10 13:56
 * @version v1.0
 * @copyright 北京辰森
 * @email xgj@choicesoft.com.cn
 */

import com.alibaba.fastjson.JSONArray;
import com.choice.cloud.architect.groot.support.PodNameCreateHandler;
import com.ctrip.framework.apollo.openapi.dto.OpenEnvClusterDTO;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @classDesc:
 * @Author: yuxiaopeng
 * @createTime: 2020/6/10 13:56
 * @version: v1.0
 * @copyright: 北京辰森
 * @email: yxp@choicesoft.com.cn
 */
public class PodNameCreateHandlerTest {
    @Test
    public void getAllEnvClusters() {

        Assert.assertEquals("basicinfo-idc01-ldc01-stable-0002-0",PodNameCreateHandler.handler(
                "basicinfo", "stable", "01", "01", 1, 1
        ));
        Assert.assertEquals("",PodNameCreateHandler.handler(
                "basicinfo", "stable", "01", "01", 1, 64
        ));
        Assert.assertEquals("",PodNameCreateHandler.handler(
                "basicinfo", "stable", "01", "01", 63, 2
        ));
        Assert.assertEquals("",PodNameCreateHandler.handler(
                "basicinfo", "stable", "01", "02", 65, 64
        ));
        Assert.assertEquals("basicinfo-idc01-ldc02-stable-0068-0,basicinfo-idc01-ldc02-stable-0067-0,basicinfo-idc01-ldc02-stable-0066-0",PodNameCreateHandler.handler(
                "basicinfo", "stable", "01", "02", 65, 3
        ));
        Assert.assertEquals("",PodNameCreateHandler.handler(
                "basicinfo", "stable", "02", "03", 129, 64
        ));
        Assert.assertEquals(
                "basicinfo-idc02-ldc03-stable-0192-0,basicinfo-idc02-ldc03-stable-0191-0,basicinfo-idc02-ldc03-stable-0190-0,basicinfo-idc02-ldc03-stable-0189-0,basicinfo-idc02-ldc03-stable-0188-0,basicinfo-idc02-ldc03-stable-0187-0,basicinfo-idc02-ldc03-stable-0186-0,basicinfo-idc02-ldc03-stable-0185-0,basicinfo-idc02-ldc03-stable-0184-0,basicinfo-idc02-ldc03-stable-0183-0,basicinfo-idc02-ldc03-stable-0182-0,basicinfo-idc02-ldc03-stable-0181-0,basicinfo-idc02-ldc03-stable-0180-0,basicinfo-idc02-ldc03-stable-0179-0,basicinfo-idc02-ldc03-stable-0178-0,basicinfo-idc02-ldc03-stable-0177-0,basicinfo-idc02-ldc03-stable-0176-0,basicinfo-idc02-ldc03-stable-0175-0,basicinfo-idc02-ldc03-stable-0174-0,basicinfo-idc02-ldc03-stable-0173-0,basicinfo-idc02-ldc03-stable-0172-0,basicinfo-idc02-ldc03-stable-0171-0,basicinfo-idc02-ldc03-stable-0170-0,basicinfo-idc02-ldc03-stable-0169-0,basicinfo-idc02-ldc03-stable-0168-0,basicinfo-idc02-ldc03-stable-0167-0,basicinfo-idc02-ldc03-stable-0166-0,basicinfo-idc02-ldc03-stable-0165-0,basicinfo-idc02-ldc03-stable-0164-0,basicinfo-idc02-ldc03-stable-0163-0,basicinfo-idc02-ldc03-stable-0162-0,basicinfo-idc02-ldc03-stable-0161-0,basicinfo-idc02-ldc03-stable-0160-0,basicinfo-idc02-ldc03-stable-0159-0,basicinfo-idc02-ldc03-stable-0158-0,basicinfo-idc02-ldc03-stable-0157-0,basicinfo-idc02-ldc03-stable-0156-0,basicinfo-idc02-ldc03-stable-0155-0,basicinfo-idc02-ldc03-stable-0154-0,basicinfo-idc02-ldc03-stable-0153-0,basicinfo-idc02-ldc03-stable-0152-0,basicinfo-idc02-ldc03-stable-0151-0,basicinfo-idc02-ldc03-stable-0150-0,basicinfo-idc02-ldc03-stable-0149-0,basicinfo-idc02-ldc03-stable-0148-0,basicinfo-idc02-ldc03-stable-0147-0,basicinfo-idc02-ldc03-stable-0146-0,basicinfo-idc02-ldc03-stable-0145-0,basicinfo-idc02-ldc03-stable-0144-0,basicinfo-idc02-ldc03-stable-0143-0,basicinfo-idc02-ldc03-stable-0142-0,basicinfo-idc02-ldc03-stable-0141-0,basicinfo-idc02-ldc03-stable-0140-0,basicinfo-idc02-ldc03-stable-0139-0,basicinfo-idc02-ldc03-stable-0138-0,basicinfo-idc02-ldc03-stable-0137-0,basicinfo-idc02-ldc03-stable-0136-0,basicinfo-idc02-ldc03-stable-0135-0,basicinfo-idc02-ldc03-stable-0134-0,basicinfo-idc02-ldc03-stable-0133-0,basicinfo-idc02-ldc03-stable-0132-0,basicinfo-idc02-ldc03-stable-0131-0,basicinfo-idc02-ldc03-stable-0130-0",PodNameCreateHandler.handler(
                "basicinfo", "stable", "02", "03", 129, 63
        ));
    }
}
