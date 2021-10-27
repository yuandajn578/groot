package com.choice.cloud.architect.groot.controller;

import com.choice.cloud.architect.groot.dto.ChangeAppEnvDTO;
import com.choice.cloud.architect.groot.dto.ListChangeDTO;
import com.choice.cloud.architect.groot.exception.ServiceException;
import com.choice.cloud.architect.groot.option.WebPage;
import com.choice.cloud.architect.groot.request.*;
import com.choice.cloud.architect.groot.response.Page;
import com.choice.cloud.architect.groot.response.ResponseData;
import com.choice.cloud.architect.groot.service.inner.GrAppEnvRelService;
import com.choice.cloud.architect.groot.service.inner.GrChangeService;
import com.choice.cloud.architect.groot.util.UserHolder;
import com.choice.driver.jwt.entity.AuthUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 应用变更操作controller
 * </p>
 *
 * @author zhangkun
 */
@Api(description = "应用变更操作",tags = "应用变更操作")
@RestController
@RequestMapping("/api/change")
@Slf4j
public class GrChangeController {
    @Autowired
    private GrChangeService grChangeService;

    @Autowired
    private GrAppEnvRelService grAppEnvRelService;

    @ApiOperation(value = "创建应用变更",notes = "创建应用变更")
    @PostMapping("/create")
    public ResponseData createChange(@RequestBody @Valid CreateChangeRequest createChangeRequest, AuthUser authUser) {
        try {
            String oid = grChangeService.createChange(createChangeRequest,authUser);
            return ResponseData.createBySuccess(oid);
        } catch (ServiceException e) {
            log.warn("GrChangeController createChange 创建变更出错 warn:{}", e);
            return ResponseData.createByError(e.getResponseInfo());
        } catch (Exception e) {
            log.error("GrChangeController createChange 创建变更出错 error:{}", e);
            return ResponseData.createByError();
        }
    }

    @ApiOperation(value = "应用变更分页查询",notes = "应用变更分页查询")
    @PostMapping("/list")
    public ResponseData<List<ListChangeDTO>> list(@RequestBody ListChangeRequest listChangeRequest, AuthUser authUser) {
        WebPage<List<ListChangeDTO>> list = grChangeService.list(listChangeRequest);
        Page page = new Page();
        page.setPageNum(list.getPageNum());
        page.setPageSize(list.getPageSize());
        page.setTotal(list.getTotal().intValue());
        return ResponseData.createBySuccess(page, list.getData());
    }

    @ApiOperation(value = "主键查询变更单信息",notes = "主键查询变更单信息")
    @ApiImplicitParam(name="oid",value = "变更单ID",dataType = "String",paramType = "Query",required = true)
    @PostMapping("/queryByOid")
    public ResponseData<ListChangeDTO> queryByOid(@RequestParam("oid") String oid){
        ListChangeDTO listChangeDTO = grChangeService.queryByOid(oid);
        return ResponseData.createBySuccess(listChangeDTO);
    }

    @ApiOperation(value = "变更单信息修改",notes = "变更单信息修改")
    @PostMapping("/updateChange")
    public ResponseData updateChange(@RequestBody UpdateChangeRequest updateChangeRequest, AuthUser authUser){
        grChangeService.updateChange(updateChangeRequest,authUser);
        return ResponseData.createBySuccess();
    }

    /**
     * 关闭变更单，修改状态
     * @param updateChangeStatusRequest
     * @param authUser
     * @return
     */
    @ApiOperation(value = "关闭变更单，修改状态", notes = "关闭变更单，修改状态")
    @PostMapping("/updateChangeStatus")
    public ResponseData updateChangeStatus(@RequestBody UpdateChangeStatusRequest updateChangeStatusRequest, AuthUser authUser){
        grChangeService.updateStatusChange(updateChangeStatusRequest,authUser);
        return ResponseData.createBySuccess();
    }

    @ApiOperation(value = "修改变更单阶段",notes = "修改变更单阶段")
    @PostMapping("/updateChangeStage")
    public ResponseData updateChangeStage(@RequestBody UpdateChangeStageRequest updateChangeStageRequest, AuthUser authUser){
        grChangeService.updateChangeStage(updateChangeStageRequest, UserHolder.getUserName(authUser.getUid()));
        return ResponseData.createBySuccess();
    }

    /**
     * 验收变更单，修改状态
     * @param updateChangeStageRequest
     * @param authUser
     * @return
     */
    @ApiOperation(value = "验收变更单，修改状态", notes = "验收变更单，修改状态，测试成功进入下一阶段")
    @PostMapping("/updateChangeTestStatus")
    public ResponseData updateChangeTestStatus(@RequestBody UpdateChangeStatusRequest updateChangeStageRequest, AuthUser authUser) {
        try {
            grChangeService.updateChangeTestStatus(updateChangeStageRequest, UserHolder.getUserName(authUser.getUid()));
            return ResponseData.createBySuccess();
        } catch (ServiceException e) {
            log.warn("GrChangeController createChange 创建变更出错 warn:{}", ExceptionUtils.getStackTrace(e));
            return ResponseData.createByError(e.getResponseInfo());
        } catch (Exception e) {
            log.error("GrChangeController createChange 创建变更出错 error:{}", ExceptionUtils.getStackTrace(e));
            return ResponseData.createByError();
        }
    }

    @ApiOperation(value = "获取变更关联环境信息",notes = "获取变更关联环境信息")
    @PostMapping("/appEnvInfo")
    public ResponseData<ChangeAppEnvDTO> getAppEnvInfo(@RequestBody ChangeAppEnvRequest request){
        ChangeAppEnvDTO changeAppEnvDTO = new ChangeAppEnvDTO();
        ListChangeDTO changeDTO = grChangeService.queryByOid(request.getChangeId());
        changeAppEnvDTO.setChangeName(changeDTO.getChangeName());
        changeAppEnvDTO.setAppEnvList(
                grAppEnvRelService.listByAppCodeAndEnv(request.getChangeId(),
                        request.getAppCode(), request.getEnvType(), null));
        return ResponseData.createBySuccess(changeAppEnvDTO);
    }

}
