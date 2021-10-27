package com.choice.cloud.architect.groot.controller;

import com.choice.cloud.architect.groot.request.AddGrMemberRequest;
import com.choice.cloud.architect.groot.request.UpdateChangeMemberRequest;
import com.choice.cloud.architect.groot.response.ResponseData;
import com.choice.cloud.architect.groot.service.inner.GrMemberService;
import com.choice.driver.jwt.entity.AuthUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * (GrMember)表控制层
 *
 * @author makejava
 * @since 2020-03-03 16:55:19
 */
@Api(description = "人员操作",tags = "人员操作")
@RestController
@RequestMapping("/api/grMember")
public class GrMemberController {

    @Resource
    private GrMemberService grMemberService;

    /**
     * 主键逻辑删除
     * @param oid
     * @return
     */
    @ApiOperation(value = "主键删除人员信息",notes = "主键删除人员信息")
    @ApiImplicitParam(name="oid",value = "人员ID",dataType = "String",paramType = "Query",required = true)
    @PostMapping("/deleteByOid")
    public ResponseData deleteByOid(@RequestParam("oid")String oid){
        grMemberService.deleteByOid(oid);
        return ResponseData.createBySuccess();
    }

    /**
     * 新增变更关联用户
     * @param request
     * @return
     */
    @ApiOperation(value = "新增变更关联用户",notes = "新增变更关联用户")
    @PostMapping("/addByChange")
    public ResponseData addChangeMember(@RequestBody AddGrMemberRequest request, AuthUser authUser){
        grMemberService.insert(request, authUser);
        return ResponseData.createBySuccess();
    }


    /**
     * 更新变更关联用户
     * @param request
     * @return
     */
    @ApiOperation(value = "更新变更关联用户",notes = "更新变更关联用户")
    @PostMapping("/updateChangeMember")
    public ResponseData updateChangeMember(@RequestBody UpdateChangeMemberRequest request, AuthUser authUser){
        grMemberService.updateChangeMemberBatch(request, authUser);
        return ResponseData.createBySuccess();
    }
}