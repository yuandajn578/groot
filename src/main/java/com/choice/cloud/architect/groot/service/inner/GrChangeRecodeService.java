package com.choice.cloud.architect.groot.service.inner;

import com.choice.cloud.architect.groot.model.GrChangeRecode;

import java.util.List;


/**
 * (GrChangeRecode)表服务接口
 *
 * @author makejava
 * @since 2020-03-03 16:55:20
 */
public interface GrChangeRecodeService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    GrChangeRecode queryById(Object id);

    /**
     * 查询多条数据
     * @return 对象列表
     */
    List<GrChangeRecode> queryAll(GrChangeRecode grChangeRecode);

    /**
     * 新增数据
     *
     * @param grChangeRecode 实例对象
     * @return 实例对象
     */
    GrChangeRecode insert(GrChangeRecode grChangeRecode);

    /**
     * 修改数据
     *
     * @param grChangeRecode 实例对象
     * @return 实例对象
     */
    GrChangeRecode update(GrChangeRecode grChangeRecode);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Object id);

}