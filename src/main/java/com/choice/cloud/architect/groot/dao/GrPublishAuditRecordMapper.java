package com.choice.cloud.architect.groot.dao;

import com.choice.cloud.architect.groot.model.GrPublishAuditRecord;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zhangkun
 */
@Mapper
@Repository
public interface GrPublishAuditRecordMapper {

    /**
     * [新增]
     **/
    int insert(GrPublishAuditRecord grPublishAuditRecord);

    /**
     * [刪除]
     **/
    int delete(int id);

    /**
     * [更新]
     **/
    int update(GrPublishAuditRecord grPublishAuditRecord);

    /**
     * [查询] 根据主键 id 查询
     **/
    GrPublishAuditRecord load(int id);

    /**
     * [查询] 分页查询
     **/
    List<GrPublishAuditRecord> pageList(int offset, int pagesize);

    /**
     * [查询] 分页查询 count
     **/
    int pageListCount(int offset,int pagesize);
}
