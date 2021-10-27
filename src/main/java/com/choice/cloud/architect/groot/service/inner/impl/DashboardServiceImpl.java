package com.choice.cloud.architect.groot.service.inner.impl;

import com.choice.cloud.architect.groot.dao.GrChangeMapper;
import com.choice.cloud.architect.groot.dao.GrPublishMapper;
import com.choice.cloud.architect.groot.dto.StatisticsBoxDTO;
import com.choice.cloud.architect.groot.enums.ChangeEnum;
import com.choice.cloud.architect.groot.enums.PublishStatusEnum;
import com.choice.cloud.architect.groot.option.GlobalConst;
import com.choice.cloud.architect.groot.remote.enums.AppRankEnum;
import com.choice.cloud.architect.groot.remote.milkyway.MilkywayService;
import com.choice.cloud.architect.groot.service.inner.DashboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: zhangguoquan
 * @Date: 2020/4/16 14:32
 */
@Service
@Slf4j
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private GrChangeMapper grChangeMapper;

    @Autowired
    private GrPublishMapper grPublishMapper;

    @Autowired
    private MilkywayService milkywayService;

    public StatisticsBoxDTO statisticsBox() {
        StatisticsBoxDTO statisticsBoxDTO = new StatisticsBoxDTO();
        Integer runningCount = grChangeMapper.countByStatus(ChangeEnum.RUNNING.getCode(), GlobalConst.NOT_DELETE);
        Integer publishingCount = grChangeMapper.countByStatus(ChangeEnum.PUBLISHING.getCode(), GlobalConst.NOT_DELETE);
        Integer publishSuccessCount = grPublishMapper.countByPublishStatus(PublishStatusEnum.SUCCESS.value(), GlobalConst.NOT_DELETE);
        Integer publishFailureCount = grPublishMapper.countByPublishStatus(PublishStatusEnum.FAILURE.value(), GlobalConst.NOT_DELETE);
        Integer commonAppCount = milkywayService.countByRank(AppRankEnum.COMMON_APP.getCode());
        Integer coreAppCount = milkywayService.countByRank(AppRankEnum.CORE_APP.getCode());
        statisticsBoxDTO.setRunningCount(runningCount);
        statisticsBoxDTO.setPublishingCount(publishingCount);
        statisticsBoxDTO.setPublishSuccessCount(publishSuccessCount);
        statisticsBoxDTO.setPublishFailureCount(publishFailureCount);
        statisticsBoxDTO.setCommonAppCount(commonAppCount);
        statisticsBoxDTO.setCoreAppCount(coreAppCount);
        return statisticsBoxDTO;
    }
}
