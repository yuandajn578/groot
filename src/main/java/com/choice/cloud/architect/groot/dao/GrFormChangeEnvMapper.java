package com.choice.cloud.architect.groot.dao;

import com.choice.cloud.architect.groot.model.GrFormChangeEnv;
import org.apache.ibatis.annotations.Param;

/**
 * @author zhangkun
 */
public interface GrFormChangeEnvMapper {
    int delete(Integer id);

    int insert(GrFormChangeEnv grFormChangeEnv);

    int insertDynamic(GrFormChangeEnv grFormChangeEnv);

    int updateDynamic(GrFormChangeEnv grFormChangeEnv);

    int update(GrFormChangeEnv grFormChangeEnv);

    GrFormChangeEnv selectById(Integer id);

    /**
     * 删除环境
     * @param envId
     * @return
     */
    int deleteEnv(@Param("envId") String envId);
}
