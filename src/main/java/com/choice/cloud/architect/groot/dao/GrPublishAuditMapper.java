package com.choice.cloud.architect.groot.dao;

import com.choice.cloud.architect.groot.dataobject.PublishAuditQueryPageDO;
import com.choice.cloud.architect.groot.model.GrPublishAudit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zhangkun
 */
@Mapper
@Repository
public interface GrPublishAuditMapper {
    /**
     * [新增]
     **/
    int insert(GrPublishAudit grPublishAudit);

    /**
     * [刪除]
     **/
    int delete(int id);

    /**
     * [更新]
     **/
    int update(GrPublishAudit grPublishAudit);

    /**
     * [查询] 根据主键 id 查询
     **/
    GrPublishAudit load(String oid);

    /**
     * 根据钉钉审批单id查询审核单详情
     * @param dtalkProcessId
     * @return
     */
    List<GrPublishAudit> selectByDtalkProcessId(String dtalkProcessId);

    /**
     * [查询] 分页查询
     **/
    List<GrPublishAudit> pageList(PublishAuditQueryPageDO publishAuditQueryPageDO);

    /**
     * [查询] 分页查询 count
     **/
    int pageListCount(int offset,int pagesize);

    int updateAuditStatus(@Param("oid") String oid, @Param("auditStatus") String auditStatus);

    GrPublishAudit getLastAuditByEnv(String envType, String envCode, List<String> ids);
}
