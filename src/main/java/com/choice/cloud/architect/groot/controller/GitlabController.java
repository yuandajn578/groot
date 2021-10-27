package com.choice.cloud.architect.groot.controller;

import com.choice.cloud.architect.groot.dto.ListPublishDTO;
import com.choice.cloud.architect.groot.dto.gitlab.CreateProjectByGroupRequestDTO;
import com.choice.cloud.architect.groot.dto.gitlab.GetAllGroupResponseDTO;
import com.choice.cloud.architect.groot.dto.gitlab.GetProjectByNamespaceAndProjectNameRequestDTO;
import com.choice.cloud.architect.groot.dto.gitlab.GetProjectByNamespaceAndProjectNameResponseDTO;
import com.choice.cloud.architect.groot.exception.ServiceException;
import com.choice.cloud.architect.groot.gitlab.GitlabService;
import com.choice.cloud.architect.groot.option.WebPage;
import com.choice.cloud.architect.groot.request.ListPublishRequest;
import com.choice.cloud.architect.groot.response.Page;
import com.choice.cloud.architect.groot.response.ResponseData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import javax.validation.Valid;

/**
 * @ClassName GitlabController
 * @Description git管理
 * @Author Guangshan Wang
 * @Date 2020/3/17/017 10:24
 */
@Api(value = "git管理", tags = "git管理")
@RestController
@RequestMapping("/api/git")
@Validated
@Slf4j
public class GitlabController {

    @Autowired
    private GitlabService gitlabService;

    @ApiOperation(value = "发布分页查询", notes = "发布分页查询")
    @GetMapping("/list")
    public ResponseData<List<String>> publishPageList(@RequestParam String gitProjectUrl) {
        try {
            List<String> branchNameList = gitlabService.getAllBranch(gitProjectUrl);
            return ResponseData.createBySuccess(branchNameList);
        } catch (ServiceException e) {
            return ResponseData.createByError(e.getResponseInfo());
        } catch (Exception e) {
            log.error("GitlabController publishPageList 查询所有git分支出错 error:{}", e);
            return ResponseData.createByError();
        }
    }

    @ApiOperation(value = "获取所有GitLab的group分组信息", notes = "获取所有GitLab的group分组信息")
    @GetMapping("/getAllGroup")
    public ResponseData<GetAllGroupResponseDTO> getAllGroup() {
        try {
            GetAllGroupResponseDTO responseDTO = gitlabService.getAllGroup();
            return ResponseData.createBySuccess(responseDTO);
        } catch (ServiceException e) {
            return ResponseData.createByError(e.getResponseInfo());
        } catch (Exception e) {
            log.error("GitlabController getAllGroup 获取所有GitLab的group分组信息出错 error:{}", ExceptionUtils.getStackTrace(e));
            return ResponseData.createByError();
        }
    }

    @ApiOperation(value = "根据GitLab的group分组创建工程Project", notes = "根据GitLab的group分组创建工程Project")
    @PostMapping("/createProjectByGroup")
    public ResponseData<Boolean> createProjectByGroup(@RequestBody @Valid CreateProjectByGroupRequestDTO requestDTO) {
        try {
            boolean createFlag = gitlabService.createProjectByGroup(requestDTO);
            return ResponseData.createBySuccess(createFlag);
        } catch (ServiceException e) {
            return ResponseData.createByError(e.getResponseInfo());
        } catch (Exception e) {
            log.error("GitlabController createProjectByGroup 根据GitLab的group分组创建工程Project出错 error:{}", ExceptionUtils.getStackTrace(e));
            return ResponseData.createByError();
        }
    }

    @ApiOperation(value = "根据GitLab的namespace和项目名称查询工程", notes = "根据GitLab的namespace和项目名称查询工程")
    @PostMapping("/getProjectByNamespaceAndProjectName")
    public ResponseData<GetProjectByNamespaceAndProjectNameResponseDTO> getProjectByNamespaceAndProjectName(@RequestBody @Valid GetProjectByNamespaceAndProjectNameRequestDTO requestDTO) {
        try {
            GetProjectByNamespaceAndProjectNameResponseDTO responseDTO = gitlabService.getProjectByNamespaceAndProjectName(requestDTO);
            return ResponseData.createBySuccess(responseDTO);
        } catch (ServiceException e) {
            return ResponseData.createByError(e.getResponseInfo());
        } catch (Exception e) {
            log.error("GitlabController getProjectByNamespaceAndProjectName 根据GitLab的namespace和项目名称查询工程 error:{}", ExceptionUtils.getStackTrace(e));
            return ResponseData.createByError();
        }
    }
}
