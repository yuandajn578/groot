package com.choice.cloud.architect.groot.dao;

import com.choice.cloud.architect.groot.model.GrBuildRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 构建记录(GrBuildRecord)表数据库访问层
 *
 * @author makejava
 * @since 2020-03-06 10:04:46
 */
@Mapper
@Repository
public interface GrBuildRecordMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param jobName
     * @param buildNum
     * @return 实例对象
     */
    GrBuildRecord queryByBuild(@Param("jobName") String jobName, @Param("buildNum") int buildNum);

    List<GrBuildRecord> listWithPage(@Param("deleteFlag") int deleteFlag, @Param("publishOid") String publishOid);

    /**
     * 新增数据
     *
     * @param grBuildRecord 实例对象
     * @return 影响行数
     */
    int insert(GrBuildRecord grBuildRecord);

    int updateResult(
            @Param("jobName") String jobName,
            @Param("buildNum") int buildNum,
            @Param("result") String result,
            @Param("duration") long duration
    );

    GrBuildRecord getLastRecordByPublishOid(@Param("publishOid") String publishOid);

    List<GrBuildRecord> getNormalBuildingLastRecord(
            @Param("appCode") String appCode, @Param("result") String result);
}