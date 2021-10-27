package com.choice.cloud.architect.groot.service.inner;

import com.choice.cloud.architect.groot.dto.ListAppPublishApplyDTO;
import com.choice.cloud.architect.groot.option.WebPage;
import com.choice.cloud.architect.groot.request.CreateAppPublishApplyRequest;
import com.choice.cloud.architect.groot.request.ListAppPublishApplyRequest;

import java.util.List;

/**
 * @author zhangkun
 */
public interface GrApplicationFormService {
    void createAppPublishApply(CreateAppPublishApplyRequest createAppPublishApplyRequest);

    WebPage<List<ListAppPublishApplyDTO>> list(ListAppPublishApplyRequest listAppPublishApplyRequest);
}
