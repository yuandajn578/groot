package com.choice.cloud.architect.groot.service.inner.impl;

import com.choice.cloud.architect.groot.dao.GrPublishBlackRecordMapper;
import com.choice.cloud.architect.groot.enums.BlackTypeEnum;
import com.choice.cloud.architect.groot.model.GrPublishBlackRecord;
import com.choice.cloud.architect.groot.option.GlobalConst;
import com.choice.cloud.architect.groot.request.AddDateBlackRecordRequest;
import com.choice.cloud.architect.groot.request.AddWeekBlackRecordRequest;
import com.choice.cloud.architect.groot.service.inner.PublishBlackListService;
import com.choice.driver.jwt.entity.AuthUser;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/21 14:53
 */
@Service
@Slf4j
public class PublishBlackListServiceImpl extends BaseService implements PublishBlackListService {

    @Autowired
    private GrPublishBlackRecordMapper grPublishBlackRecordMapper;

    @Override
    public boolean publishTimeIsInBlackList(LocalDateTime publishTime) {

        int weekCode = publishTime.getDayOfWeek().getValue();
        LocalTime time = publishTime.toLocalTime();
        LocalDate date = publishTime.toLocalDate();
        log.info("Publish Time = {}", publishTime);
        GrPublishBlackRecord record = grPublishBlackRecordMapper.queryByDate(date, GlobalConst.NOT_DELETE);
        if (record == null) {
            record = grPublishBlackRecordMapper.queryByWeek(weekCode, time, GlobalConst.NOT_DELETE);
        }

        return record != null;
    }

    @Override
    public List<GrPublishBlackRecord> getBlackList(String blackType) {
        return grPublishBlackRecordMapper.queryBlackList(blackType, GlobalConst.NOT_DELETE);
    }

    @Override
    public void addWeekBlackRecord(AddWeekBlackRecordRequest request, AuthUser authUser) {
        GrPublishBlackRecord record = new GrPublishBlackRecord();
        initSaveModel(record, authUser);
        record.setBlackType(BlackTypeEnum.WEEK.name());
        record.setWeekCode(request.getWeekCode());
        record.setBeginTime(request.getBeginTime());
        record.setEndTime(request.getEndTime());
        grPublishBlackRecordMapper.insert(record);
    }

    @Override
    public void addDateBlackRecord(AddDateBlackRecordRequest request, AuthUser authUser) {
        List<GrPublishBlackRecord> grPublishBlackRecordList = Lists.newArrayList();
        for (LocalDate date :request.getDateList() ) {
            GrPublishBlackRecord record = new GrPublishBlackRecord();
            initSaveModel(record, authUser);
            record.setBlackType(BlackTypeEnum.DATE.name());
            record.setBlackDate(date);
            grPublishBlackRecordList.add(record);
        }
        grPublishBlackRecordMapper.batchInsert(grPublishBlackRecordList);
    }
}
