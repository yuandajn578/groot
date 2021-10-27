package com.choice.cloud.architect.groot.remote.milkyway;

import com.choice.cloud.architect.groot.exception.ServiceException;
import com.choice.cloud.architect.groot.option.WebPage;
import com.choice.cloud.architect.groot.remote.*;
import com.choice.cloud.architect.groot.remote.enums.RoleEnum;
import com.choice.cloud.architect.groot.response.ResponseData;
import com.choice.cloud.architect.groot.response.code.MilkyWayResponseCode;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/7 20:42
 */
@Service
@Slf4j
public class MilkywayService {

    @Autowired
    private MilkywayClient milkywayClient;

    public AppConfigInfoResponseDTO getAppConfigInfoByAppCode(String appCode) {
        ResponseData<AppConfigInfoResponseDTO> responseData = milkywayClient.getAppConfigInfoByAppCode(appCode);
        return responseData.getData();
    }

    public AppListResponse getAppInfo(String appCode){
        ResponseData<AppListResponse> response = milkywayClient.getApplicationInfo(appCode);
        AppListResponse applicationInfo = response.getData();
        if(null == applicationInfo){
            throw new ServiceException(MilkyWayResponseCode.NO_APPINFO_OF_MILKYWAY);
        }
        return applicationInfo;
    }

    public List<AppConfigRelationDTO> getConfigList(AppListResponse appListResponse){
        List<AppConfigRelationDTO> configOptionList = appListResponse.getConfigOptionList();
//        if(CollectionUtils.isEmpty(configOptionList)){
//            throw new ServiceException(MilkyWayResponseCode.NO_CONFIGS_OF_APOLLO);
//        }
        return configOptionList;
    }

    public String getConfigByName(String configName, String defaultValue, List<AppConfigRelationDTO> configList) {
        Map<String, List<AppConfigRelationDTO>> configMap = configList.stream().collect(Collectors.groupingBy(AppConfigRelationDTO::getConfigName));
        List<AppConfigRelationDTO> configValues = configMap.get(configName);
        if(CollectionUtils.isEmpty(configValues)){
            return defaultValue;
        }
        return configValues.get(0).getValue();
    }

    public List<AppListResponse> getAppListByUser(String userId) {
        ApplicationListByUserParam param = new ApplicationListByUserParam();
        param.setUserId(userId);
        // 全角色
        param.setRoleEnumList(Lists.newArrayList(RoleEnum.values()));
        ResponseData<List<AppListResponse>> responseData = milkywayClient.listAppByUser(param);
        return responseData.getData();
    }

    public List<AppListResponse> getAppListByCodes(Set<String> codes) {
        ApplicationListByAppCodeParam param = new ApplicationListByAppCodeParam();
        param.setAppCodeSet(codes);
        ResponseData<List<AppListResponse>> responseData = milkywayClient.listByAppCode(param);
        return responseData.getData();
    }

    public List<AppListResponse> getAppList() {
        WebPage<AppListRequest> param = new WebPage<>();
        param.setPageNum(1);
        param.setPageSize(1000);
        AppListRequest appListRequest = new AppListRequest();
        // TODO: 2020/5/25 只能查到已审核的，应用创建审核流程完善后修改
        appListRequest.setStatus("CONFIRMED");
        param.setData(appListRequest);
        ResponseData<WebPage<List<AppListResponse>>> responseData = milkywayClient.appList(param);
        if (responseData.getData() != null) {
            return responseData.getData().getData();
        } else {
            log.warn("没有符合条件的app response code = {}", responseData.getCode());
        }
        return Collections.emptyList();

    }

    public Integer countByRank(String rank) {
        ResponseData<Integer> responseData = milkywayClient.countByRank(rank);
        return responseData.getData();
    }

    public DomainArchitectureDTO getDomainArchitectureInfo(String id) {
        SingleOidParam param = new SingleOidParam();
        param.setOid(id);
        ResponseData<DomainArchitectureDTO> data = milkywayClient.getDomainArchitectureInfo(param);
        return data.getData();
    }
}
