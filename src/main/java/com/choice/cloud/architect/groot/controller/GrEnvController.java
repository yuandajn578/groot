package com.choice.cloud.architect.groot.controller;

import com.choice.cloud.architect.groot.dto.*;
import com.choice.cloud.architect.groot.enums.EnvTypeEnum;
import com.choice.cloud.architect.groot.exception.ServiceException;
import com.choice.cloud.architect.groot.option.WebPage;
import com.choice.cloud.architect.groot.request.*;
import com.choice.cloud.architect.groot.request.k8s.PodLogRequest;
import com.choice.cloud.architect.groot.response.Page;
import com.choice.cloud.architect.groot.response.ResponseData;
import com.choice.cloud.architect.groot.response.code.EnvResponseCode;
import com.choice.cloud.architect.groot.response.code.MilkyWayResponseCode;
import com.choice.cloud.architect.groot.service.inner.AppManagePermissionService;
import com.choice.cloud.architect.groot.service.inner.ContainerManageService;
import com.choice.cloud.architect.groot.service.inner.GrEnvService;
import com.choice.cloud.architect.groot.service.inner.GrResourceSpecService;
import com.choice.driver.jwt.entity.AuthUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 应用环境操作controller
 * </p>
 *
 * @author zhangkun
 */
@Api(description = "应用环境操作", tags = "应用环境操作")
@RestController
@RequestMapping("/api/env")
public class GrEnvController {
    @Autowired
    private GrEnvService grEnvService;
    @Autowired
    private ContainerManageService containerManageService;
    @Autowired
    private GrResourceSpecService grResourceSpecService;
    @Autowired
    private AppManagePermissionService appManagePermissionService;

    @ApiOperation(value = "创建应用环境", notes = "创建应用环境")
    @PostMapping("/create")
    public ResponseData createEnv(@Valid @RequestBody CreateEnvRequest createEnvRequest, AuthUser authUser) {
        grEnvService.createEnv(createEnvRequest, authUser);
        return ResponseData.createBySuccess();
    }

    @ApiOperation(value = "应用环境分页查询", notes = "应用环境分页查询")
    @PostMapping("/list")
    public ResponseData<List<ListEnvDTO>> listEnv(@RequestBody ListEnvRequest listEnvRequest) {
        WebPage<List<ListEnvDTO>> list = grEnvService.list(listEnvRequest);
        Page page = new Page();
        page.setPageNum(list.getPageNum());
        page.setPageSize(list.getPageSize());
        page.setTotal(list.getTotal().intValue());
        return ResponseData.createBySuccess(page, list.getData());
    }

    @ApiOperation(value = "删除环境", notes = "删除环境")
    @DeleteMapping("/del/{id}")
    public ResponseData delEnv(@PathVariable("id") String id, AuthUser authUser) {
        grEnvService.delEnv(id, authUser);
        return ResponseData.createBySuccess();
    }

    @ApiOperation(value = "修改环境状态", notes = "修改环境状态")
    @PostMapping("/changeStatus")
    public ResponseData changeEnvStatus(@Valid @RequestBody ChangeEnvStatusRequest request, AuthUser authUser) {
        grEnvService.changeStatus(request, authUser);
        return ResponseData.createBySuccess();
    }

    @ApiOperation(value = "应用环境下拉列表查询", notes = "应用环境下拉列表查询")
    @PostMapping("/select/list")
    public ResponseData<List<SelectListEnvDTO>> selectListEnv(@RequestBody ListEnvRequest listEnvRequest) {
        List<SelectListEnvDTO> list = grEnvService.selectList(listEnvRequest);
        return ResponseData.createBySuccess(list);
    }

    @ApiOperation(value = "查询应用下环境数据", notes = "查询应用下环境数据")
    @PostMapping("/app")
    public ResponseData<Map<String, List<ListAppEnvDTO>>> listEnvByApp(@Valid @RequestBody ListEnvByAppRequest request) {
        Map<String, List<ListAppEnvDTO>> appEnvDetailMap = grEnvService.listEnvByApp(request);

        return ResponseData.createBySuccess(appEnvDetailMap);
    }

    @ApiOperation(value = "查询某个应用指定环境分组下的详情", notes = "查询某个应用指定环境分组下的详情")
    @PostMapping("/app/detail")
    public ResponseData getDetailByAppAndEnv(@RequestParam("oid") String oid, @RequestParam("appCode") String appCode) {
        AppEnvDetailDTO appEnvDetailDTO = grEnvService.getDetailByAppAndEnv(oid, appCode);

        return ResponseData.createBySuccess(appEnvDetailDTO);
    }

    @ApiOperation(value = "查询某个应用在某个环境分组下的资源信息")
    @PostMapping("/app/resourceList")
    public ResponseData listAppResource(@Valid @RequestBody ListAppEnvResourceRequest request) {
        QueryPodListRequest queryPodListRequest = new QueryPodListRequest();
        queryPodListRequest.setNamespaceName(request.getEnvType().concat("-").concat(request.getEnvCode()));
        queryPodListRequest.setAppCode(request.getAppCode());

        List<ListPodInfoDTO> podListDTOList = containerManageService.podList(queryPodListRequest);
        return ResponseData.createBySuccess(podListDTOList);
    }

    @ApiOperation(value = "删除指定应用资源")
    @PostMapping("/app/resource/delete")
    public ResponseData<Boolean> deleteAppResource(@Valid @RequestBody DeletePodRequest request, AuthUser authUser) {

        String podName = request.getPodName();
        String tempStr = podName.substring(0, podName.lastIndexOf("-"));
        String appCode = tempStr.substring(0, tempStr.lastIndexOf("-"));
        appManagePermissionService.checkPermission(authUser, appCode);
        return ResponseData.createBySuccess(containerManageService.deletePod(request.getNamespace(), request.getPodName(), authUser));
    }

    @Deprecated
    @ApiOperation(value = "重启")
    @PostMapping("/app/redeploy")
    public ResponseData<Boolean> redeploy(@Valid @RequestBody AppRedeployRequest request, AuthUser authUser) {
        if (EnvTypeEnum.pro.value().equalsIgnoreCase(request.getEnvType())) {
            throw new ServiceException(EnvResponseCode.REDEPLOY_ONLINE);
        }
        containerManageService.redeploy(request.getEnvType().concat("-").concat(request.getEnvCode()), request.getAppCode(), authUser);
        return ResponseData.createBySuccess();
    }

    @ApiOperation(value = "滚动更新部署")
    @PostMapping("/app/rollingDeploy")
    public ResponseData<Boolean> rollingDeploy(@Valid @RequestBody AppRedeployRequest request, AuthUser authUser) {
        appManagePermissionService.checkPermission(authUser, request.getAppCode());
        containerManageService.rollingDeploy(request.getEnvType().concat("-").concat(request.getEnvCode()), request.getAppCode(), authUser);
        return ResponseData.createBySuccess();
    }

    /**
     * 保存环境配置
     * @param request
     * @param authUser
     * @return
     */
    @ApiOperation(value = "编辑应用环境")
    @PostMapping("/edit/save")
    public ResponseData editEnv(@Valid @RequestBody EditEnvRequest request, AuthUser authUser) {
        grEnvService.editEnv(request, authUser);

        return ResponseData.createBySuccess();
    }

    @ApiOperation(value = "查询指定环境已关联的应用")
    @PostMapping("/bind/app")
    public ResponseData getBindApp(@Valid @RequestBody QueryEnvBindAppRequest request) {
        List<String> envBindAppList = grEnvService.getEnvBindApp(request);
        return ResponseData.createBySuccess(envBindAppList);
    }

    @ApiOperation(value = "保存计算资源类型")
    @PostMapping("/resourceSpec/create")
    public ResponseData createResourceSpec(@Valid @RequestBody CreateResourceSpecRequest request, AuthUser authUser) {
        grResourceSpecService.save(request, authUser);
        return ResponseData.createBySuccess();
    }

    @ApiOperation(value = "查询计算资源规格列表")
    @GetMapping("/resourceSpec/list")
    public ResponseData listResourceSpec() {
        List<ResourceSpecDTO> resourceSpecList = grResourceSpecService.list();

        return ResponseData.createBySuccess(resourceSpecList);
    }

    @ApiOperation(value = "查询应用默认发布配置")
    @GetMapping("/app/defaultPublishConfig")
    public ResponseData getDefaultPublishConfig() {
        GrAppEnvDefaultConfigDTO grAppEnvDefaultConfigDTO = new GrAppEnvDefaultConfigDTO();

        return ResponseData.createBySuccess(grAppEnvDefaultConfigDTO);
    }

    @ApiOperation(value = "获取指定应用指定环境分组下的发布配置")
    @GetMapping("/app/publishConfig")
    public ResponseData getAppPublishConfig(@Valid @RequestBody QueryAppPublishConfigRequest request) {
        GrAppEnvDefaultConfigDTO appPublishConfig = grEnvService.getAppPublishConfig(request);

        return ResponseData.createBySuccess(appPublishConfig);
    }

    @ApiOperation(value = "查询一个变更关联的项目联调环境")
    @GetMapping("/change/bind")
    public ResponseData getChangeBindEnv(@RequestParam("changeId") String changeId) {
        List<ChangeBindEnvDTO> changeBindEnv = grEnvService.getChangeBindEnv(changeId);

        return ResponseData.createBySuccess(changeBindEnv);
    }

    @ApiOperation(value = "将变更加入联调项目环境")
    @PostMapping("/change/bind/add")
    public ResponseData addChangeBindEnv(@Valid @RequestBody AddChangeBindEnvRequest request, AuthUser authUser) {
        grEnvService.addChangeBindEnv(request, authUser);

        return ResponseData.createBySuccess();
    }

    @ApiOperation(value = "查询一个项目联调环境下关联的变更")
    @PostMapping("/change/list")
    public ResponseData getEnvBindChangeList(@Valid @RequestBody QueryEnvBindChangeRequest request, AuthUser authUser) {
        WebPage<List<ListChangeDTO>> envBindChangeList = grEnvService.getEnvBindChangeList(request, authUser);
        Page page = new Page();
        page.setPageNum(envBindChangeList.getPageNum());
        page.setPageSize(envBindChangeList.getPageSize());
        page.setTotal(envBindChangeList.getTotal().intValue());

        return ResponseData.createBySuccess(page, envBindChangeList.getData());
    }

    @ApiOperation(value = "将变更从联调项目环境移除")
    @PostMapping("/change/bind/remove")
    public ResponseData removeChangeBindEnv(@Valid @RequestBody RemoveChangeBindEnvRequest request) {
        grEnvService.removeChangeBindEnv(request);

        return ResponseData.createBySuccess();
    }

    @ApiOperation(value = "应用环境下拉列表查询", notes = "应用环境下拉列表查询")
    @PostMapping("/select/list/group")
    public ResponseData<Map<String, List<SelectListEnvDTO>>> selectListEnvGroup(@RequestBody ListEnvRequest listEnvRequest) {
        Map<String, List<SelectListEnvDTO>> typeEnvMap = grEnvService.selectListEnvGroup(listEnvRequest);

        return ResponseData.createBySuccess(typeEnvMap);
    }

    @ApiOperation(value = "查询pod日志", notes = "查询pod日志")
    @PostMapping("/pod/log")
    public ResponseData<String> getPodLog(@RequestBody PodLogRequest request) {
        return ResponseData.createBySuccess(containerManageService.getLog(request));
    }
}
