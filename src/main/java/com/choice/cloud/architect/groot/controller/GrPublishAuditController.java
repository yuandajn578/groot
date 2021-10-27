package com.choice.cloud.architect.groot.controller;

import com.choice.cloud.architect.groot.dto.*;
import com.choice.cloud.architect.groot.process.config.ProcessConfig;
import com.choice.cloud.architect.groot.process.service.ProcessService;
import com.choice.cloud.architect.groot.request.CreatePublishAuditRequest;
import com.choice.cloud.architect.groot.request.EditPublishAuditRequest;
import com.choice.cloud.architect.groot.request.ListPublishAuditRequest;
import com.choice.cloud.architect.groot.response.ResponseData;
import com.choice.cloud.architect.groot.service.inner.GrPublishAuditService;
import com.choice.driver.jwt.entity.AuthUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.rmi.ServerException;
import java.util.List;

/**
 * @author zhangkun
 */
@Slf4j
@Api(tags = "发布审核单管理")
@RestController
@RequestMapping(value = "/api/publish/audit")
public class GrPublishAuditController {
    @Autowired
    private GrPublishAuditService grPublishAuditService;
    @Autowired
    private ProcessService processService;
    @Resource
    private ProcessConfig processConfig;


    @ApiOperation(value = "创建发布审核单", notes = "创建发布审核单")
    @PostMapping("/create")
    public ResponseData createPublishAudit(@Valid @RequestBody CreatePublishAuditRequest request, AuthUser authUser) {
        grPublishAuditService.createPublishAudit(request, authUser);
        return ResponseData.createBySuccess();
    }

    /**
     * 列表
     */
    @ApiOperation(value = "发布审核单列表", notes = "发布审核单列表")
    @PostMapping("/list")
    public ResponseData<List<ListPublishAuditDTO>> listPublishAudit(@Valid @RequestBody ListPublishAuditRequest request) {
        return grPublishAuditService.pageQueryPublishAudit(request);
    }

    /**
     * 根据钉钉审核单id查询审批进度
     */
    @ApiOperation(value = "根据钉钉审核单id查询审批进度", notes = "根据钉钉审核单id查询审批进度")
    @PostMapping("/getProcessListByDTalkId")
    public ResponseData getProcessListByDTalkId(@Valid @RequestBody AuditDTalkProcessListRequestDTO request) {
        AuditDTalkProcessListResponseDTO responseDTO = grPublishAuditService.getProcessListByDTalkId(request);
        return ResponseData.createBySuccess(responseDTO);
    }

    /**
     * 获取单个发布审核单详情
     */
    @ApiOperation(value = "获取单个发布审核单的详情", notes = "获取单个发布审核单的详情")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "oid",
                    value = "发布审核单id",
                    required = true,
                    dataType = "String"
            )
    })
    @PostMapping("/detail")
    public ResponseData<PublishAuditDetailDTO> getPublishAuditDetail(@RequestParam("oid") String oid) {
        PublishAuditDetailDTO publishAuditDetail = grPublishAuditService.getPublishAuditDetail(oid);
        return ResponseData.createBySuccess(publishAuditDetail);
    }

    /**
     * 编辑 - 待提交和已取消的状态下都可以修改
     */
    @ApiOperation(value = "编辑发布审核单", notes = "编辑发布审核单")
    @PostMapping("/edit")
    public ResponseData editPublishAudit(@Valid @RequestBody EditPublishAuditRequest request, AuthUser authUser) {
        grPublishAuditService.editPublishAudit(request, authUser);
        return ResponseData.createBySuccess();
    }

    /**
     * 提交
     */
    @ApiOperation(value = "提交发布审核单", notes = "提交发布审核单")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "oid",
                    value = "发布审核单id",
                    required = true,
                    dataType = "String"
            )
    })

    @PostMapping("/submit")
    public ResponseData submitPublishAudit(@RequestParam("oid") String oid, AuthUser authUser) {
        grPublishAuditService.submitPublishAudit(oid, authUser);
        return ResponseData.createBySuccess();
    }

    /**
     * 撤销
     */
    @ApiOperation(value = "撤销发布审核单", notes = "撤销发布审核单")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "oid",
                    value = "发布审核单id",
                    required = true,
                    dataType = "String"
            )
    })
    @PostMapping("/cancel")
    public ResponseData cancelPublishAudit(@RequestParam("oid") String oid) {
        grPublishAuditService.cancelPublishAudit(oid);
        return ResponseData.createBySuccess();
    }

    @ApiOperation(value = "创建并提交发布审核单（编辑页提交）", notes = "创建并提交发布审核单（编辑页提交）")
    @PostMapping("/createAndSubmit")
    public ResponseData createAndSubmitPublishAudit(@Valid @RequestBody CreatePublishAuditRequest request, AuthUser authUser) {
        grPublishAuditService.createAndSubmitPublishAudit(request, authUser);
        return ResponseData.createBySuccess();
    }

    /**
     * 只有待提交和已取消状态的发布审核单才可以被关联新的变更单
     */
    @ApiOperation(value = "关联发布审核单", notes = "关联发布审核单")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "changeId",
                    value = "变更单id",
                    required = true,
                    dataType = "String"
            ),
            @ApiImplicitParam(
                    name = "publishAuditId",
                    value = "发布审核单id",
                    required = true,
                    dataType = "String"
            )
    })
    @PostMapping("/linkChange")
    public ResponseData linkChange(@RequestParam("changeId") String changeId, @RequestParam("publishAuditId") String publishAuditId, AuthUser authUser) {
        grPublishAuditService.linkChange(changeId, publishAuditId, authUser);
        return ResponseData.createBySuccess();
    }

    @PostMapping("/compensate")
    public ResponseData<String> compensate(@RequestParam String processInstanceId, @RequestHeader String sign) throws Exception {

        if (!sign.equals("woicewoice8824")) {
            throw new ServerException("无权限！！");
        }
        CompensateDTO compensateDTO = new CompensateDTO();
        compensateDTO.setProcessInstanceId(processInstanceId);
        compensateDTO.setEventType("bpms_instance_change");
        compensateDTO.setProcessCode(processConfig.getProcessCode());
        compensateDTO.setType("finish");
        compensateDTO.setResult("agree");
        log.info("补偿参数:{}", compensateDTO);
        ObjectMapper objectMapper = new ObjectMapper();
        processService.handleProcessMqMsg4Compensate(objectMapper.writeValueAsString(compensateDTO));
        return ResponseData.createBySuccess();
    }
}
