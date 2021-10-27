package com.choice.cloud.architect.groot.service.inner.impl;

import com.choice.cloud.architect.groot.dao.GrChangeRecodeMapper;
import com.choice.cloud.architect.groot.enums.ChangeOptEnum;
import com.choice.cloud.architect.groot.model.GrChangeRecode;
import com.choice.cloud.architect.groot.service.inner.GrChangeRecodeService;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


/**
 * (GrChangeRecode)表服务实现类
 *
 * @author makejava
 * @since 2020-03-03 17:08:04
 */
@Service("grChangeRecodeService")
public class GrChangeRecodeServiceImpl implements GrChangeRecodeService {
    @Resource
    private GrChangeRecodeMapper grChangeRecodeDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public GrChangeRecode queryById(Object id) {
        return this.grChangeRecodeDao.queryById(id);
    }

    /**
     * 查询多条数据
     * @return 对象列表
     */
    @Override
    public List<GrChangeRecode> queryAll(GrChangeRecode grChangeRecode) {
        List<GrChangeRecode> grChangeRecodes = grChangeRecodeDao.listWithPage(grChangeRecode);
        List<String> list = Lists.newArrayList();
        ChangeOptEnum[] values = ChangeOptEnum.values();
        for (int i = 0; i < values.length; i++) {
            ChangeOptEnum value = values[i];
            list.add(value.name());
        }
        grChangeRecodes.forEach(ele -> {
            String optType = ele.getOptType();
            if(list.contains(optType)){
                String splitContent = "";
                if(!"".equals(ele.getOldValue())&& !"".equals(ele.getNewValue())){
                    splitContent = "变更为";
                }
                String content = ele.getOldValue() + " "+splitContent+" "+ ele.getNewValue();
                ele.setOldValue(content);
            }
        });
        return grChangeRecodes;
    }

    /**
     * 新增数据
     *
     * @param grChangeRecode 实例对象
     * @return 实例对象
     */
    @Override
    public GrChangeRecode insert(GrChangeRecode grChangeRecode) {
        this.grChangeRecodeDao.insert(grChangeRecode);
        return grChangeRecode;
    }

    /**
     * 修改数据
     *
     * @param grChangeRecode 实例对象
     * @return 实例对象
     */
    @Override
    public GrChangeRecode update(GrChangeRecode grChangeRecode) {
        this.grChangeRecodeDao.update(grChangeRecode);
        return this.queryById(grChangeRecode.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Object id) {
        return this.grChangeRecodeDao.deleteById(id) > 0;
    }
}