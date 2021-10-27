package com.choice.cloud.architect.groot.dao;

import com.choice.cloud.architect.groot.model.GrApplicationForm;

import java.util.List;

/**
 * @author zhangkun
 */
public interface GrApplicationFormMapper {
    int delete(Integer id);

    int insert(GrApplicationForm grApplicationForm);

    int insertDynamic(GrApplicationForm grApplicationForm);

    int updateDynamic(GrApplicationForm grApplicationForm);

    int update(GrApplicationForm grApplicationForm);

    GrApplicationForm selectById(Integer id);

    List<GrApplicationForm> listWithPage();
}
