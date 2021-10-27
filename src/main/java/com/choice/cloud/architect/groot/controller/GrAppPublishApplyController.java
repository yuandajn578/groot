package com.choice.cloud.architect.groot.controller;

import com.choice.cloud.architect.groot.dto.ListAppPublishApplyDTO;
import com.choice.cloud.architect.groot.option.WebPage;
import com.choice.cloud.architect.groot.request.CreateAppPublishApplyRequest;
import com.choice.cloud.architect.groot.request.ListAppPublishApplyRequest;
import com.choice.cloud.architect.groot.response.Page;
import com.choice.cloud.architect.groot.response.ResponseData;
import com.choice.cloud.architect.groot.service.inner.GrApplicationFormService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 应用构建申请单controller
 * </p>
 *
 * @author zhangkun
 */
@Api(description = "应用构建申请单",tags = "应用构建申请单")
@RestController
@RequestMapping("/api/appPublishApply")
public class GrAppPublishApplyController {
    @Autowired
    private GrApplicationFormService grApplicationFormService;

    @ApiOperation(value = "创建应用构建申请单",notes = "创建应用构建申请单")
    @PostMapping("/create")
    public ResponseData createAppPublishApply(@RequestBody CreateAppPublishApplyRequest createAppPublishApplyRequest) {
        grApplicationFormService.createAppPublishApply(createAppPublishApplyRequest);
        return ResponseData.createBySuccess();
    }

    @ApiOperation(value = "应用构建申请单分页查询",notes = "应用构建申请单分页查询")
    @PostMapping("/list")
    public ResponseData<List<ListAppPublishApplyDTO>> listAppPublishApply(@RequestBody ListAppPublishApplyRequest listAppPublishApplyRequest) {
        WebPage<List<ListAppPublishApplyDTO>> list = grApplicationFormService.list(listAppPublishApplyRequest);
        Page page = new Page();
        page.setPageNum(list.getPageNum());
        page.setPageSize(list.getPageSize());
        page.setTotal(list.getTotal().intValue());
        return ResponseData.createBySuccess(page, list.getData());
    }
}
