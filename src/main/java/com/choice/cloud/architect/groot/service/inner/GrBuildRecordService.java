package com.choice.cloud.architect.groot.service.inner;

import com.choice.cloud.architect.groot.dto.ListBuildRecordDTO;
import com.choice.cloud.architect.groot.model.GrBuildRecord;
import com.choice.cloud.architect.groot.option.WebPage;
import com.choice.cloud.architect.groot.request.ListBuildRecordRequest;
import com.choice.driver.jwt.entity.AuthUser;

import java.util.List;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/6 11:29
 */
public interface GrBuildRecordService {

    WebPage<List<ListBuildRecordDTO>> pageList(ListBuildRecordRequest recordRequest);

    void saveBuildRecord(String jobName, String publishOid, String appCode, String result,
                         int buildNum, String version, AuthUser authUser);

    void updateRecordResult(String jobName, int buildNum);

    GrBuildRecord getLastRecordByPublishOid(String publishOid);

    boolean existNormalBuilding(String appCode);
}
