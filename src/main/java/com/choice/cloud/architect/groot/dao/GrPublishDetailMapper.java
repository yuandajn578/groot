package com.choice.cloud.architect.groot.dao;

import com.choice.cloud.architect.groot.model.GrPublishDetail;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zhangkun
 */
@Mapper
@Repository
public interface GrPublishDetailMapper {
    int delete(Integer id);

    int insert(GrPublishDetail grPublishDetail);

    int insertDynamic(GrPublishDetail grPublishDetail);

    int updateDynamic(GrPublishDetail grPublishDetail);

    int update(GrPublishDetail grPublishDetail);

    GrPublishDetail selectById(Integer id);

    List<GrPublishDetail> selectByPublishId(String publishId);
}
