package com.choice.cloud.architect.groot.controller;

import com.choice.cloud.architect.groot.dto.ListPublishDetailDTO;
import com.choice.cloud.architect.groot.response.ResponseData;
import com.choice.cloud.architect.groot.service.inner.GrPublishDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zhangkun
 */
@RestController
@RequestMapping(value = "/api/publish/detail")
public class PublishDetailController {
    @Autowired
    private GrPublishDetailService grPublishDetailService;

    @RequestMapping("/get")
    public ResponseData<List<ListPublishDetailDTO>> getDetail(@RequestParam("publishId") String publishId) {
        List<ListPublishDetailDTO> listPublishDetailDTOList = grPublishDetailService.listPublishDetail(publishId);
        return ResponseData.createBySuccess(listPublishDetailDTOList);
    }
}
