package com.choice.cloud.architect.groot.service.inner.impl;

import com.choice.cloud.architect.groot.dao.GrAppEnvRelMapper;
import com.choice.cloud.architect.groot.dao.GrEnvMapper;
import com.choice.cloud.architect.groot.dto.AppEnvRelDTO;
import com.choice.cloud.architect.groot.dto.ChangeBindEnvDTO;
import com.choice.cloud.architect.groot.dto.ListChangeDTO;
import com.choice.cloud.architect.groot.enums.EnvTypeEnum;
import com.choice.cloud.architect.groot.exception.ServiceException;
import com.choice.cloud.architect.groot.model.GrAppEnvRel;
import com.choice.cloud.architect.groot.model.GrEnv;
import com.choice.cloud.architect.groot.option.GlobalConst;
import com.choice.cloud.architect.groot.request.AddChangeBindEnvRequest;
import com.choice.cloud.architect.groot.request.RemoveChangeBindEnvRequest;
import com.choice.cloud.architect.groot.response.code.SysResponseCode;
import com.choice.cloud.architect.groot.service.inner.GrAppEnvRelService;
import com.choice.cloud.architect.groot.service.inner.GrChangeService;
import com.choice.cloud.architect.groot.util.CommonUtil;
import com.choice.driver.jwt.entity.AuthUser;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhangkun
 */
@Service
@Slf4j
public class GrAppEnvRelServiceImpl extends BaseService implements GrAppEnvRelService {
    @Autowired
    private GrAppEnvRelMapper grAppEnvRelMapper;

    @Autowired
    private GrEnvMapper grEnvMapper;

    @Autowired
    private GrChangeService grChangeService;

    @Override
    public void saveAppAndEnvRel(String changeId, String appCode, String envType, String envCode, AuthUser authUser) {
        if (StringUtils.isBlank(appCode) || StringUtils.isBlank(envCode) || StringUtils.isBlank(envType)) {
            throw new IllegalArgumentException("appCode or envCode or envType is blank");
        }

        GrAppEnvRel oldGrAppEnvRel = grAppEnvRelMapper.selectByAppCodeAndEnvCode(changeId, appCode, envType, envCode);
        if (null != oldGrAppEnvRel) {
            return;
        }

        GrEnv grEnv = grEnvMapper.selectByCode(envType, envCode);
        GrAppEnvRel grAppEnvRel = new GrAppEnvRel();
        initSaveModel(grAppEnvRel, authUser);
        grAppEnvRel.setAppCode(appCode);
        grAppEnvRel.setEnvType(envType);
        grAppEnvRel.setEnvCode(envCode);
        grAppEnvRel.setEnvName(grEnv.getEnvName());
        grAppEnvRel.setChangeId(changeId);

        grAppEnvRelMapper.insert(grAppEnvRel);
    }

    @Override
    public List<GrAppEnvRel> listByAppCode(List<String> appCodeList) {
        return grAppEnvRelMapper.selectByAppCode(appCodeList);
    }

    @Override
    public List<GrAppEnvRel> listByEnv(String envType, String envCode) {
        return grAppEnvRelMapper.selectByEnv(envType, envCode);
    }

    @Override
    public void initDefaultValue( String appCode) {
        GrAppEnvRel grAppEnvRelTest = grAppEnvRelMapper.selectByAppCodeAndEnvCode("-1", appCode, EnvTypeEnum.test.name(), "stable");
        GrAppEnvRel grAppEnvRelPre = grAppEnvRelMapper.selectByAppCodeAndEnvCode("-1", appCode, EnvTypeEnum.pro.name(), EnvTypeEnum.pre.name());
        GrAppEnvRel grAppEnvRelPro = grAppEnvRelMapper.selectByAppCodeAndEnvCode("-1", appCode, EnvTypeEnum.pro.name(), "stable");

        if(grAppEnvRelTest != null || grAppEnvRelPre != null || grAppEnvRelPro != null){
            log.info("appCode = {}, 默认环境分组已存在", appCode);
        }

        GrAppEnvRel appEnvRelTest = newGrAppEnvRel();
        appEnvRelTest.setChangeId("-1");
        appEnvRelTest.setAppCode(appCode);
        appEnvRelTest.setEnvType(EnvTypeEnum.test.name());
        appEnvRelTest.setEnvCode("stable");
        GrEnv grEnvTest = grEnvMapper.selectByCode(appEnvRelTest.getEnvType(), appEnvRelTest.getEnvCode());
        appEnvRelTest.setEnvName(grEnvTest.getEnvName());

        GrAppEnvRel appEnvRelPre = newGrAppEnvRel();
        appEnvRelPre.setChangeId("-1");
        appEnvRelPre.setAppCode(appCode);
        appEnvRelPre.setEnvType(EnvTypeEnum.pro.name());
        appEnvRelPre.setEnvCode("pre");
        GrEnv grEnvPre = grEnvMapper.selectByCode(appEnvRelPre.getEnvType(), appEnvRelPre.getEnvCode());
        appEnvRelPre.setEnvName(grEnvPre.getEnvName());

        GrAppEnvRel appEnvRelPro = newGrAppEnvRel();
        appEnvRelPro.setChangeId("-1");
        appEnvRelPro.setAppCode(appCode);
        appEnvRelPro.setEnvType(EnvTypeEnum.pro.name());
        appEnvRelPro.setEnvCode("stable");
        GrEnv grEnvPro = grEnvMapper.selectByCode(appEnvRelPro.getEnvType(), appEnvRelPro.getEnvCode());
        appEnvRelPro.setEnvName(grEnvPro.getEnvName());

        List<GrAppEnvRel> list = Lists.newArrayListWithCapacity(3);
        // 防止重新建立应用时，重复关联环境
        addItemInAppEnvRelList(list, appEnvRelTest);
        addItemInAppEnvRelList(list, appEnvRelPre);
        addItemInAppEnvRelList(list, appEnvRelPro);

        if (CollectionUtils.isNotEmpty(list)) {
            grAppEnvRelMapper.batchInsert(list);
        }
    }

    private void addItemInAppEnvRelList(List<GrAppEnvRel> list, GrAppEnvRel appEnvRel) {
        GrAppEnvRel oldGrAppEnvRel = grAppEnvRelMapper
                .selectByAppCodeAndEnvCode(appEnvRel.getChangeId(), appEnvRel.getAppCode(),
                        appEnvRel.getEnvType(), appEnvRel.getEnvCode());
        if (null == oldGrAppEnvRel) {
            list.add(appEnvRel);
        }
    }

    private GrAppEnvRel newGrAppEnvRel() {
        GrAppEnvRel grAppEnvRel = new GrAppEnvRel();

        grAppEnvRel.setOid(CommonUtil.getUUIdUseMongo());
        grAppEnvRel.setCreateUser("robot");
        grAppEnvRel.setCreateTime(LocalDateTime.now());
        grAppEnvRel.setUpdateUser("robot");
        grAppEnvRel.setUpdateTime(LocalDateTime.now());
        grAppEnvRel.setDeleteFlag(GlobalConst.NOT_DELETE);

        return grAppEnvRel;
    }

    @Override
    public List<GrAppEnvRel> listByAppCodeAndEnv(String changeId, String appCode, String envType, String envCode) {

        return grAppEnvRelMapper.listByAppCodeAndEnvCode(changeId,appCode,envType,envCode);
    }

    @Override
    public List<ChangeBindEnvDTO> getChangeBindEnv(String changeId) {
        if (StringUtils.isBlank(changeId)) {
            throw new ServiceException(SysResponseCode.PARAM_ILLEGAL);
        }

        List<ChangeBindEnvDTO> changeBindEnvDTOList = Lists.newArrayList();
        List<GrAppEnvRel> appEnvRels = grAppEnvRelMapper.listByChangeId(changeId);

        if (CollectionUtils.isNotEmpty(appEnvRels)) {
            for (GrAppEnvRel appEnvRel : appEnvRels) {
                ChangeBindEnvDTO changeBindEnvDTO = new ChangeBindEnvDTO();

                changeBindEnvDTO.setChangeId(changeId);
                changeBindEnvDTO.setEnvType(appEnvRel.getEnvType());
                changeBindEnvDTO.setEnvCode(appEnvRel.getEnvCode());

                changeBindEnvDTOList.add(changeBindEnvDTO);
            }
        }

        return changeBindEnvDTOList;
    }

    @Override
    public void addChangeBindEnv(AddChangeBindEnvRequest request, AuthUser authUser) {
        ListChangeDTO grChangeDTO = grChangeService.queryByOid(request.getChangeId());

        if (null == grChangeDTO) {
            throw new ServiceException("应用变更不存在");
        }

        GrAppEnvRel existedChangeEnvRel = grAppEnvRelMapper.selectByAppCodeAndEnvCode(request.getChangeId(), grChangeDTO.getAppCode(), request.getEnvType(), request.getEnvCode());

        if (null != existedChangeEnvRel) {
            log.info("changeId = {}, appCode = {}, envType = {}, envCode = {} 已存在", request.getChangeId(), grChangeDTO.getAppCode(), request.getEnvType(), request.getEnvCode());
            return;
        }

        GrEnv env = grEnvMapper.selectByCode(request.getEnvType(), request.getEnvCode());
        GrAppEnvRel grAppEnvRel = new GrAppEnvRel();
        initSaveModel(grAppEnvRel, authUser);
        grAppEnvRel.setChangeId(request.getChangeId());
        grAppEnvRel.setAppCode(grChangeDTO.getAppCode());
        grAppEnvRel.setEnvName(env.getEnvName());
        grAppEnvRel.setEnvCode(request.getEnvCode());
        grAppEnvRel.setEnvType(request.getEnvType());

        grAppEnvRelMapper.insert(grAppEnvRel);
    }

    @Override
    public void removeChangeBindEnv(RemoveChangeBindEnvRequest request) {
        if (StringUtils.isBlank(request.getEnvType()) || StringUtils.isBlank(request.getEnvCode())) {
            throw new ServiceException(SysResponseCode.PARAM_ILLEGAL);
        }

        grAppEnvRelMapper.deleteByChangeIdAndEnv(request.getChangeId(), request.getEnvType(), request.getEnvCode());
    }

    @Override
    public void delAppChangeEnvRel(String envType, String envCode) {

    }

    @Override
    public List<AppEnvRelDTO> queryAppEnvRelByEnvType(String envType) {
        List<GrAppEnvRel> rels = grAppEnvRelMapper.selectByEnvType(envType);
        return rels.stream().map(rel -> {
            AppEnvRelDTO appEnvRelDTO = new AppEnvRelDTO();
            BeanUtils.copyProperties(rel, appEnvRelDTO);
            return appEnvRelDTO;
        }).collect(Collectors.toList());
    }
}
