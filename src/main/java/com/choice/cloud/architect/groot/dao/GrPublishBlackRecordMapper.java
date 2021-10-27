package com.choice.cloud.architect.groot.dao;

import com.choice.cloud.architect.groot.model.GrPublishBlackRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/21 16:27
 */
@Mapper
@Repository
public interface GrPublishBlackRecordMapper {

    GrPublishBlackRecord queryByWeek(@Param("weekCode") int weekCode, @Param("time") LocalTime time,
                                     @Param("deleteFlag") int deleteFlag);

    GrPublishBlackRecord queryByDate(@Param("date") LocalDate date, @Param("deleteFlag") int deleteFlag);

    List<GrPublishBlackRecord> queryBlackList(@Param("blackType") String blackType, @Param("deleteFlag") int deleteFlag);

    int insert(GrPublishBlackRecord grPublishBlackRecord);

    int batchInsert(List<GrPublishBlackRecord> list);


}
