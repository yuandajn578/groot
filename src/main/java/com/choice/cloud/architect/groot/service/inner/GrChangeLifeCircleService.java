package com.choice.cloud.architect.groot.service.inner;

import com.choice.cloud.architect.groot.dto.GrChangeLifeCirclePageDTO;
import com.choice.cloud.architect.groot.model.GrChangeLifeCircle;
import com.choice.cloud.architect.groot.option.WebPage;
import com.choice.cloud.architect.groot.request.PageChangeLifeCircleRequest;

import java.util.List;
import java.util.Map;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/13 19:07
 */
public interface GrChangeLifeCircleService {

    int save(GrChangeLifeCircle grChangeLifeCircle);

    WebPage<List<GrChangeLifeCirclePageDTO>> pageList(PageChangeLifeCircleRequest request);

    Map<String, List<GrChangeLifeCirclePageDTO>> envTypeList(PageChangeLifeCircleRequest request);
}
