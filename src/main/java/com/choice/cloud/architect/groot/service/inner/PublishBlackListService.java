package com.choice.cloud.architect.groot.service.inner;

import com.choice.cloud.architect.groot.model.GrPublishBlackRecord;
import com.choice.cloud.architect.groot.request.AddDateBlackRecordRequest;
import com.choice.cloud.architect.groot.request.AddWeekBlackRecordRequest;
import com.choice.driver.jwt.entity.AuthUser;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/21 14:51
 */
public interface PublishBlackListService {

    boolean publishTimeIsInBlackList(LocalDateTime publishTime);

    List<GrPublishBlackRecord> getBlackList(String blackType);

    void addWeekBlackRecord(AddWeekBlackRecordRequest request, AuthUser authUser);

    void addDateBlackRecord(AddDateBlackRecordRequest request, AuthUser authUser);
}
