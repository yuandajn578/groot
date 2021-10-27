package com.choice.cloud.architect.groot.support;

import com.choice.cloud.architect.groot.enums.PodControllerTypeEnum;
import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author zhangkun
 */
@Slf4j
@Component
public class AppDeployPodControllerTypeChooser {
    @Value("${deploy.useStatefulSet.whiteItem}")
    private String useStatefulSetWhiteItem;

    public PodControllerTypeEnum choose(String appCode) {
        if (StringUtils.isBlank(appCode)) {
            throw new IllegalArgumentException("appCode is empty");
        }

        if (StringUtils.isBlank(useStatefulSetWhiteItem)) {
            log.info("AppDeployPodControllerTypeChooser, app = {}, podControllerType = {}", appCode, PodControllerTypeEnum.DEPLOYMENT.value());
            return PodControllerTypeEnum.DEPLOYMENT;
        }

        List<String> useStatefulSetAppList = Splitter.on(",").splitToList(useStatefulSetWhiteItem);

        if (useStatefulSetAppList.contains(appCode)) {
            log.info("AppDeployPodControllerTypeChooser, app = {}, podControllerType = {}", appCode, PodControllerTypeEnum.STATEFUL_SET.value());
            return PodControllerTypeEnum.STATEFUL_SET;
        } else {
            log.info("AppDeployPodControllerTypeChooser, app = {}, podControllerType = {}", appCode, PodControllerTypeEnum.DEPLOYMENT.value());
            return PodControllerTypeEnum.DEPLOYMENT;
        }
    }
}
