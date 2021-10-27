package com.choice.cloud.architect.groot.service.inner.impl;

import com.alibaba.fastjson.JSON;
import com.choice.cloud.architect.groot.constants.PublishConstants;
import com.choice.cloud.architect.groot.dto.ListPodInfoDTO;
import com.choice.cloud.architect.groot.dto.OrchestratePodWhenScaleUpDTO;
import com.choice.cloud.architect.groot.dto.PublishPodDetailDTO;
import com.choice.cloud.architect.groot.exception.ServiceException;
import com.choice.cloud.architect.groot.option.GlobalConst;
import com.choice.cloud.architect.groot.request.QueryPodListRequest;
import com.choice.cloud.architect.groot.service.inner.ContainerManageService;
import com.choice.cloud.architect.groot.service.inner.IdcSelector;
import com.choice.cloud.architect.groot.service.inner.LdcPublishService;
import com.choice.cloud.architect.groot.service.inner.PodOrchestrationService;
import com.choice.cloud.architect.groot.support.PodNameCreateHandler;
import com.choice.cloud.architect.groot.support.PodNameParser;
import com.choice.cloud.architect.groot.util.NamespaceUtils;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import static com.choice.cloud.architect.groot.enums.EnvTypeEnum.pro;
import static com.choice.cloud.architect.groot.enums.EnvTypeEnum.test;
import static com.choice.cloud.architect.groot.response.code.PublishResponseCode.APP_NOT_DEPLOYED;

/**
 * @author zhangkun
 */
@Service
@Slf4j
public class PodOrchestrationServiceImpl implements PodOrchestrationService {
    @Autowired
    private LdcPublishService ldcPublishService;

    @Autowired
    private ContainerManageService containerManageService;

    @Resource(name = "randomOfflineIdcSelector")
    private IdcSelector offlineIdcSelector;

    @Resource(name = "randomOnlineIdcSelector")
    private IdcSelector onlineIdcSelector;

    @Override
    public String orchestratePod(String envType, String envCode, String appName, String isFirstPublish, List<String> podNameList) {
        // 如果是首次发布
        if (GlobalConst.TRUE.equals(isFirstPublish)) {
            // 线上还是线下
            if (test.value().equals(envType)) {
                String idc = offlineIdcSelector.select();
                String ldc = ldcPublishService.randomLdc(envType, idc);

                String podName = PodNameCreateHandler.handler(appName, envCode, idc, ldc, 0, PublishConstants.DEFAULT_OFFLINE_PUBLISH_POD_NUM);
                PublishPodDetailDTO publishPodDetailDTO = podOrchestrationObject(idc, ldc, podName);

                String podOrchestrationJson = JSON.toJSONString(Lists.newArrayList(publishPodDetailDTO));
                log.info("PodOrchestration got json: {}", podOrchestrationJson);
                return podOrchestrationJson;
            } else if (pro.value().equals(envType)) {
                List<PublishPodDetailDTO> publishPodDetailList = Lists.newArrayList();

                for (int i = 0; i < PublishConstants.DEFAULT_ONLINE_PUBLISH_POD_NUM; i++) {
                    String idc = onlineIdcSelector.select();
                    String ldc = ldcPublishService.randomLdc(envType, idc);

                    String podName = PodNameCreateHandler.handler(appName, envCode, idc, ldc, 0, 1);
                    PublishPodDetailDTO publishPodDetailDTO = podOrchestrationObject(idc, ldc, podName);
                    publishPodDetailList.add(publishPodDetailDTO);
                }

                String podOrchestrationJson = JSON.toJSONString(publishPodDetailList);
                log.info("PodOrchestration got json: {}", podOrchestrationJson);
                return podOrchestrationJson;
            } else {
                throw new IllegalArgumentException("不合法的环境类型");
            }
        } else {
            String podOrchestrationJson = getPodOrchestrateJsonInfo(podNameList);
            log.info("PodOrchestration got json: {}", podOrchestrationJson);
            return podOrchestrationJson;
        }
    }

    @Override
    public String orchestratePodWhenScaleUp(OrchestratePodWhenScaleUpDTO dto) {
        QueryPodListRequest queryPodListRequest = new QueryPodListRequest();
        String namespaceName = NamespaceUtils.getNamespace(dto.getEnvType(), dto.getEnvCode());
        queryPodListRequest.setNamespaceName(namespaceName);
        queryPodListRequest.setAppCode(dto.getAppCode());

        // 当前应用部署信息
        List<ListPodInfoDTO> podListDTOList = containerManageService.podList(queryPodListRequest);

        if (CollectionUtils.isEmpty(podListDTOList)) {
            throw new ServiceException(APP_NOT_DEPLOYED);
        }

        OptionalInt optionalInt = podListDTOList.stream()
                .filter(e -> e.getIdc().equals(dto.getIdc()) && e.getLdc().equals(dto.getLdc()))
                .map(ListPodInfoDTO::getPodName)
                .mapToInt(i -> {
                    PodNameParser podNameParser = new PodNameParser(i);
                    return Integer.valueOf(podNameParser.getNumFlag());
                })
                .max();

        int maxNum = 0;
        if (optionalInt.isPresent()) {
            maxNum = optionalInt.getAsInt();
        }

        Optional<ListPodInfoDTO> optionalListPodInfoDTO = podListDTOList.stream().
                filter(e -> e.getIdc().equals(dto.getIdc()) && e.getLdc().equals(dto.getLdc()))
                .findFirst();

        ListPodInfoDTO listPodInfoDTO = optionalListPodInfoDTO.orElseThrow(() -> new ServiceException("Pod部署状态发生变化，扩容失败"));
        String firstPodName = listPodInfoDTO.getPodName();

        String existedStatefulSetName = firstPodName.substring(0, firstPodName.length() - 2);

        String newPodNames = PodNameCreateHandler.handler(dto.getAppCode(), dto.getEnvCode(), dto.getIdc(), dto.getLdc(), maxNum, dto.getScaleUpNum());

        List<String> podNameList = Splitter.on(",").splitToList(newPodNames);

        log.info("ScaleUp podNameList: {}", JSON.toJSONString(podNameList));
        for (String podName : podNameList) {
            containerManageService.createStatefulSetResource(namespaceName, existedStatefulSetName, podName.substring(0, podName.length() - 2));
        }

        return getPodOrchestrateJsonInfo(podNameList);
    }

    private PublishPodDetailDTO podOrchestrationObject(String idc, String ldc, String podName) {
        PublishPodDetailDTO publishPodDetailDTO = new PublishPodDetailDTO();
        publishPodDetailDTO.setPodName(podName);
        publishPodDetailDTO.setIdc(idc);
        publishPodDetailDTO.setLdc(ldc);

        return publishPodDetailDTO;
    }

    private String getPodOrchestrateJsonInfo(List<String> podNameList) {
        List<PublishPodDetailDTO> publishPodDetailList = Lists.newArrayList();

        for (String podName : podNameList) {
            PodNameParser podNameParser = new PodNameParser(podName);
            String idc = podNameParser.getIdc();
            String ldc = podNameParser.getLdc();
            PublishPodDetailDTO publishPodDetailDTO = podOrchestrationObject(idc, ldc, podName);

            publishPodDetailList.add(publishPodDetailDTO);
        }

        return JSON.toJSONString(publishPodDetailList);
    }
}
