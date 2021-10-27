package com.choice.cloud.architect.groot.dao;

import com.choice.cloud.architect.groot.dataobject.UnbindPublishAuditChangeRelDO;
import com.choice.cloud.architect.groot.model.GrPublishAuditChangeRel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zhangkun
 */
@Mapper
@Repository
public interface GrPublishAuditChangeRelMapper {
    int delete(Integer id);

    int insert(GrPublishAuditChangeRel grPublishAuditChangeRel);

    int insertDynamic(GrPublishAuditChangeRel grPublishAuditChangeRel);

    int updateDynamic(GrPublishAuditChangeRel grPublishAuditChangeRel);

    int update(GrPublishAuditChangeRel grPublishAuditChangeRel);

    GrPublishAuditChangeRel selectById(Integer id);

    int unbindRel(UnbindPublishAuditChangeRelDO unbindPublishAuditChangeRelDO);

    List<GrPublishAuditChangeRel> getByPublishAuditId(String publishAuditId);

    List<GrPublishAuditChangeRel> getByChangeId(String changeId);
}
