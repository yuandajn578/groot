package com.choice.cloud.architect.groot.controller;

import com.choice.cloud.architect.groot.dto.*;
import com.choice.cloud.architect.groot.exception.ServiceException;
import com.choice.cloud.architect.groot.option.WebPage;
import com.choice.cloud.architect.groot.request.*;
import com.choice.cloud.architect.groot.response.Page;
import com.choice.cloud.architect.groot.response.ResponseData;
import com.choice.cloud.architect.groot.service.inner.AppManagePermissionService;
import com.choice.cloud.architect.groot.service.inner.GrPublishService;
import com.choice.cloud.architect.groot.util.UserHolder;
import com.choice.driver.jwt.entity.AuthUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @Author: zhangguoquan
 * @Date: 2020/3/3 17:14
 */
@Api(value = "发布管理", tags = "发布管理")
@RestController
@RequestMapping("/api/publish")
@Slf4j
public class GrPublishController {

    @Autowired
    private GrPublishService grPublishService;

    @Autowired
    private AppManagePermissionService appManagePermissionService;

    @ApiOperation(value = "发布分页查询", notes = "发布分页查询")
    @PostMapping("/list")
    public ResponseData<List<ListPublishDTO>> publishPageList(@Valid @RequestBody ListPublishRequest request) {
        WebPage<List<ListPublishDTO>> list = grPublishService.pageList(request);
        Page page = new Page();
        page.setPageNum(list.getPageNum());
        page.setPageSize(list.getPageSize());
        page.setTotal(list.getTotal().intValue());
        return ResponseData.createBySuccess(page, list.getData());
    }

    @ApiOperation(value = "发布编辑", notes = "发布编辑")
    @PostMapping("/update")
    public ResponseData updatePublish(@Valid @RequestBody UpdatePublishRequest request, AuthUser authUser) {
        grPublishService.update(request, authUser);
        return ResponseData.createBySuccess();
    }

    @ApiOperation(value = "更改测试状态", notes = "更改测试状态")
    @PostMapping("/updateTestStatus")
    public ResponseData updateTestStatus(@Valid @RequestBody UpdatePublishTestStatusRequest request, AuthUser authUser) {
        try {
            String updateUserName = UserHolder.getUserName(authUser.getUid());
            grPublishService.updateTestStatusAndMergeMaster(request, updateUserName);
        } catch (ServiceException e) {
            return ResponseData.createByError(e.getResponseInfo());
        } catch (Exception e) {
            log.error("GcPublishController updateTestStatus 更改测试状态出错 error:{}", e);
            return ResponseData.createByError();
        }
        return ResponseData.createBySuccess();
    }

    @ApiOperation(value = "发布详情", notes = "发布详情")
    @GetMapping("/info")
    public ResponseData<PublishInfoDTO> getPublishInfo(@RequestParam String oid) {
        return ResponseData.createBySuccess(grPublishService.getByOid(oid));
    }

    @ApiOperation(value = "关闭发布", notes = "关闭发布")
    @PostMapping("/close")
    public ResponseData closePublish(@RequestParam String oid, AuthUser authUser) {
        grPublishService.close(oid, authUser);
        return ResponseData.createBySuccess();
    }

    //@Deprecated
    //@ApiOperation(value = "发布构建", notes = "发布构建")
    //@PostMapping("/build")
    //public ResponseData<BuildResultDTO> build(@Valid @RequestBody PublishBuildRequest request, AuthUser authUser) {
    //    return ResponseData.createBySuccess(grPublishService.build(request, authUser));
    //}

    @ApiOperation(value = "执行发布流程", notes = "执行发布流程")
    @PostMapping("/execute")
    public ResponseData<BuildResultDTO> execute(@Valid @RequestBody PublishRequest request, AuthUser authUser) {
        return ResponseData.createBySuccess(grPublishService.execute(request, authUser));
    }


    @ApiOperation(value = "cicd详情publishId", notes = "cicd详情")
    @GetMapping("/cicd/info")
    public ResponseData<BuildInfoDTO> cicdInfo(@RequestParam String oid) {
        BuildInfoDTO buildInfoDTO = grPublishService.getBuildInfo(oid);
        return ResponseData.createBySuccess(buildInfoDTO);
    }

    @ApiOperation(value = "cicd详情appCode envType envCode", notes = "cicd详情")
    @GetMapping("/cicd/infoByAppEnv")
    public ResponseData<BuildInfoDTO> cicdInfoByAppEnv(@RequestParam String appCode, @RequestParam String envType, @RequestParam String envCode) {
        BuildInfoDTO buildInfoDTO = grPublishService.getBuildInfo(appCode, envType, envCode);
        return ResponseData.createBySuccess(buildInfoDTO);
    }

    @ApiOperation(value = "停止构建", notes = "停止构建")
    @PostMapping("build/stop")
    public ResponseData<String> stopBuild(@RequestParam String jobName, @RequestParam int buildNum) {
        return ResponseData.createBySuccess(grPublishService.stopBuildByNumber(jobName, buildNum));
    }

    @ApiOperation(value = "获取节点日志", notes = "获取节点日志")
    @GetMapping("build/node/log")
    public ResponseData<String> getBuildNodeLog(
            @RequestParam String jobName,
            @RequestParam int buildNum,
            @RequestParam int nodeId
    ) {

        String nodeLog = grPublishService.getBuildNodeLog(jobName, buildNum, nodeId);
        return ResponseData.createBySuccess(nodeLog);
    }

    @ApiOperation(value = "k8s 回滚至上一个版本")
    @PostMapping("deployment/rollback")
    public ResponseData<Boolean> deploymentRollback(@Valid @RequestBody DeploymentRollbackRequest request, AuthUser authUser) {
        appManagePermissionService.checkPermission(authUser, request.getAppCode());
        grPublishService.rollBackToPreviousRevision(request, authUser);
        return ResponseData.createBySuccess(true);
    }

    @ApiOperation(value = "k8s 回滚指定发布")
    @PostMapping("/rollback")
    public ResponseData<Boolean> deploymentPublishRollback(@Valid @RequestBody DeploymentRollbackRequest request, AuthUser authUser) {
        appManagePermissionService.checkPermission(authUser, request.getAppCode());
        grPublishService.rollbackToSpecifiedRevision(request, authUser);
        return ResponseData.createBySuccess(true);
    }

    @ApiOperation(value = "应用在指定环境下是否是第一次发布")
    @GetMapping("/first/check")
    public ResponseData<String> checkFirstPublish(@RequestParam String appCode, @RequestParam String envType, @RequestParam String envCode) {
        String result = grPublishService.checkFirstPublish(appCode, envType, envCode);

        return ResponseData.createBySuccess(result);
    }

    @ApiOperation(value = "查询应用环境部署信息")
    @GetMapping("/app/deploy/info")
    public ResponseData<List<ApplicationDeployInfoDTO>> getApplicationDeployInfo(@RequestParam String appCode, @RequestParam String envType, @RequestParam String envCode) {
        List<ApplicationDeployInfoDTO> applicationDeployInfo = grPublishService.getApplicationDeployInfo(appCode, envType, envCode);

        return ResponseData.createBySuccess(applicationDeployInfo);
    }

    @ApiOperation(value = "指定节点数扩容")
    @PostMapping("/pod/scaleUp")
    public ResponseData scaleUpPod(@Valid @RequestBody ScaleUpPodRequest request, AuthUser authUser) {
        grPublishService.scaleUpPod(request, authUser);
        return ResponseData.createBySuccess();
    }
}
