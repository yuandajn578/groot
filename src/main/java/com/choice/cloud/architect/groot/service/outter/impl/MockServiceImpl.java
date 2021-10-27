package com.choice.cloud.architect.groot.service.outter.impl;

import com.alibaba.fastjson.JSON;
import com.aliyun.openservices.ons.api.SendResult;
import com.choice.cloud.architect.groot.dao.MockMapper;
import com.choice.cloud.architect.groot.response.ResponseData;
import com.choice.cloud.architect.groot.service.outter.MockService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @classDesc:
 * @author:yuxiaopeng
 * @createTime: 2019-02-26
 * @version: v1.0.0
 * @email: yxpchoicesoft.com.cn
 */
@Slf4j
@Service
@AllArgsConstructor
public class MockServiceImpl implements MockService {
    private MockMapper mockMapper;

    @Override
    public ResponseData test() {

        Integer count = mockMapper.selectNametById("1");

        return ResponseData.createBySuccess();
    }
}
