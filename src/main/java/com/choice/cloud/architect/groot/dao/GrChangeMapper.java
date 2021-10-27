package com.choice.cloud.architect.groot.dao;

import com.choice.cloud.architect.groot.model.GrChange;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * gr_change表数据库操作Mapper
 * </p>
 *
 * @author zhangkun
 */
@Mapper
@Repository
public interface GrChangeMapper {
    int delete(Integer id);

    int insert(GrChange grChange);

    int insertDynamic(GrChange grChange);

    int updateDynamic(GrChange grChange);

    int update(GrChange grChange);

    GrChange selectById(String oid);

    List<GrChange> selectByIds(@Param("ids") List<String> ids);

    List<GrChange> listWithPage(GrChange grChange);

    List<GrChange> list(GrChange grChange);

    int linkPublishAudit(@Param("publishAuditId") String publishAuditId, @Param("changeId") String changeId);

    int unLinkPublishAudit(@Param("publishAuditId") String publishAuditId, @Param("changeId") String changeId);

    /**
     * 修改变更单状态
     * @param grChange
     * @return
     */
    int updateChangeStatus(GrChange grChange);

    /**
     * 修改变更单阶段
     * @param grChange
     * @return
     */
    int updateChangeStage(GrChange grChange);

    int countByStatus(@Param("status") Integer status, @Param("deleteFlag") int deleteFlag);
}
