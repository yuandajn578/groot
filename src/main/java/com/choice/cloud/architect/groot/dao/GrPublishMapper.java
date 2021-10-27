package com.choice.cloud.architect.groot.dao;

import com.choice.cloud.architect.groot.dataobject.LastPublishQueryDO;
import com.choice.cloud.architect.groot.dataobject.PublishDeploymentRevisionQueryDO;
import com.choice.cloud.architect.groot.dataobject.UpdatePublishStatusDO;
import com.choice.cloud.architect.groot.model.GrPublish;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface GrPublishMapper {

    int insert(GrPublish grPublish);

    int insertDynamic(GrPublish grPublish);

    int updateDynamic(GrPublish grPublish);

    int update(GrPublish grPublish);

    GrPublish selectById(@Param("oid") String oid);

    GrPublish lastByPublish(@Param("appCode") String appCode, @Param("envType") String envType,
                            @Param("envCode") String envCode, @Param("deleteFlag") int deleteFlag,
                            @Param("publishStatus") String publishStatus);

    GrPublish lastByPublishRevision(PublishDeploymentRevisionQueryDO publishDeploymentRevisionQueryDO);

    GrPublish lastByChangeAndEnv(LastPublishQueryDO lastPublishQueryDO);

    GrPublish lastByAppEnv(@Param("appCode") String appCode, @Param("envType") String envType,
                           @Param("envCode") String envCode, @Param("deleteFlag") int deleteFlag);

    GrPublish lastByOperatorId(@Param("operatorId") String operatorId, @Param("deleteFlag") int deleteFlag);

    List<GrPublish> listWithPage(GrPublish grPublish);

    List<GrPublish> listByAuditId(@Param("publishAuditId") String publishAuditId, @Param("deleteFlag") int deleteFlag);

    int countByPublishStatus(@Param("publishStatus") String publishStatus, @Param("deleteFlag") int deleteFlag);

    void updatePublishStatusByChangeIdAndEnv(UpdatePublishStatusDO updatePublishStatusDO);

}
