package com.choice.cloud.architect.groot.gitlab;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotBlank;

import com.alibaba.fastjson.JSON;

import com.choice.cloud.architect.groot.dto.gitlab.CreateProjectByGroupRequestDTO;
import com.choice.cloud.architect.groot.dto.gitlab.GetAllGroupResponseDTO;
import com.choice.cloud.architect.groot.dto.gitlab.GetProjectByNamespaceAndProjectNameRequestDTO;
import com.choice.cloud.architect.groot.dto.gitlab.GetProjectByNamespaceAndProjectNameResponseDTO;
import com.choice.cloud.architect.groot.exception.ServiceException;
import com.choice.cloud.architect.groot.gitlab.dto.Commit;
import com.choice.cloud.architect.groot.gitlab.dto.MergeRequest;
import com.choice.cloud.architect.groot.gitlab.enums.GitMergeStatusEnum;
import com.choice.cloud.architect.groot.remote.AppListResponse;
import com.choice.cloud.architect.groot.remote.milkyway.GetInfoByAppCodeRequestDTO;
import com.choice.cloud.architect.groot.remote.milkyway.GetInfoByAppCodeResponseDTO;
import com.choice.cloud.architect.groot.remote.milkyway.MilkywayClient;
import com.choice.cloud.architect.groot.response.ResponseData;
import com.choice.cloud.architect.groot.response.code.GitlabResponseCode;
import com.choice.cloud.architect.groot.response.code.SysResponseCode;
import com.choice.cloud.architect.groot.util.HanyupinyinUtil;
import com.choice.cloud.architect.groot.util.JsonListUtil;
import com.choice.cloud.architect.groot.util.JsonUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.Pagination;
import org.gitlab.api.models.GitlabAccessLevel;
import org.gitlab.api.models.GitlabBranch;
import org.gitlab.api.models.GitlabCommit;
import org.gitlab.api.models.GitlabGroup;
import org.gitlab.api.models.GitlabMergeRequest;
import org.gitlab.api.models.GitlabProject;
import org.gitlab.api.models.GitlabProjectMember;
import org.gitlab.api.models.GitlabUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_NOT_ACCEPTABLE;
import static java.net.HttpURLConnection.HTTP_OK;

/**
 * @ClassName GitlabService
 * @Description gitlab服务
 * @Author Guangshan Wang
 * @Date 2020/3/16/016 10:16
 */
@Service
@Slf4j
public class GitlabService {

    GitlabAPI gitlabApi ;

    @Autowired
    private MilkywayClient milkywayClient;

    @Value("${client.perpetual.token}")
    private String token;

    private static final String baseUrl = "http://gitlab.choicesoft.com.cn/api/v4/projects/";
    /**
     * 检查MR状态的重试次数
     */
    private static final Integer MAX_RETRY_COUNT = 3;

    @Value("${max.query.git.commit.page.count}")
    private Integer MAX_QUERY_GIT_COMMIT_PAGE_COUNT = 5;

    /**
     * 项目启动
     * @throws Exception
     */
    @PostConstruct
    public void connectGitlab() {
        long startTime = System.currentTimeMillis();
        gitlabApi = GitlabAPI.connect(GitlabConfig.HOST_URL, GitlabConfig.API_TOKEN);
        log.info("GitlabService connectGitlab 链接git服务成功,耗时：{}", System.currentTimeMillis() - startTime);
    }

    /**
     * 查询git项目
     * @param projectUrl
     * @return
     */
    private GitlabProject getProject(String projectUrl) {
        if (projectUrl == null) {
            throw new ServiceException(SysResponseCode.PARAM_ILLEGAL);
        }
        try {
            String urlFirstSplit[] = projectUrl.split("/");
            String gitName = urlFirstSplit[urlFirstSplit.length - 1];
            String projectName = gitName.substring(0, gitName.length() - ".git".length());
            String urlSecondSplit[] = projectUrl.substring(0, projectUrl.length() - (gitName.length() + 1)).split(":");
            String namespace = urlSecondSplit[1];
            log.info("GitlabService getProject 查询git项目 projectName:{},namespace:{},projectUrl:{}", projectName, namespace, projectUrl);
            GitlabProject project = gitlabApi.getProject(namespace, projectName);
            return project;
        } catch (Exception e) {
            log.error("没有查到git仓库 projectUrl:{}", projectUrl);
            throw new ServiceException(GitlabResponseCode.NO_GIT_REP);
        }
    }

    /**
     * 创建分支
     * git@gitlab.choicesoft.com.cn:saas/hht.git
     * @param projectUrl
     * @param newBranchName 如果为空，则程序自动生成分支名
     * @return
     */
    public String createBranchRetMaster(String projectUrl, String newBranchName) {
        log.info("GitlabService createBranch 创建分支，项目git地址 projectUrl:{}", projectUrl);
        if (projectUrl == null) {
            throw new ServiceException(SysResponseCode.PARAM_ILLEGAL);
        }
        try {

            GitlabProject project = this.getProject(projectUrl);
            if (project == null) {
                log.error("GitlabService createBranch 没有找到git仓库，无法创建分支 ");
                throw new ServiceException(GitlabResponseCode.NO_GIT_REP);
            }
            if (newBranchName == null) {
                SimpleDateFormat formatter= new SimpleDateFormat("yyyyMMddHHmmss");
                Date date = new Date(System.currentTimeMillis());
                newBranchName = "release_" + formatter.format(date);
            }
            long startTime = System.currentTimeMillis();
            gitlabApi.createBranch(project, newBranchName, "master");
            log.info("创建新分支成功 branchName:{}, 耗时:{}", newBranchName, System.currentTimeMillis() - startTime);
            return newBranchName;
        } catch (Exception e) {
            throw new ServiceException(GitlabResponseCode.CREATE_BRANCH_ERROR);
        }

    }

    /**
     * 合并到master
     * @param projectUrl
     * @param sourceBranch
     * @param targetBranch
     * @param title
     */
    public void merge1(String projectUrl, String sourceBranch, String targetBranch, String title) {
        log.info("GitlabService mergeToMaster 合并分支，项目git地址 projectUrl:{},sourceBranch:{},targetBranch:{}", projectUrl, sourceBranch, targetBranch);
        if (projectUrl == null || sourceBranch == null || targetBranch == null) {
            throw new ServiceException(SysResponseCode.PARAM_ILLEGAL);
        }
        try {
            if (sourceBranch.equals(targetBranch)) {
                return ;
            }
            GitlabProject project = this.getProject(projectUrl);
            if (project == null) {
                log.error("GitlabService mergeToMaster 没有找到git仓库，无法创建分支 ");
                throw new ServiceException(GitlabResponseCode.NO_GIT_REP);
            }
            log.info("GitlabService mergeToMaster 查询项目详情： project:{}", JSON.toJSON(project));
            GitlabMergeRequest mergeRequest = null;
            List<GitlabMergeRequest> mergeRequestList = gitlabApi.getOpenMergeRequests(project);
            if (mergeRequestList != null && mergeRequestList.size() > 0) {
                for (GitlabMergeRequest request : mergeRequestList) {
                    if (sourceBranch.equals(request.getSourceBranch()) && targetBranch.equals(request.getTargetBranch())) {
                        mergeRequest = request;
                    }
                }
            }
            if (mergeRequest == null) {
                log.info("GitlabService mergeToMaster 创建mergeRequest sourceBranch:{},projectUrl:{}", sourceBranch, projectUrl);
                mergeRequest = gitlabApi.createMergeRequest(project.getId(), sourceBranch, targetBranch, GitlabConfig.USER_ID, title);
            }
            log.info("GitlabService mergeToMaster 合并分支请求内容 mergeRequest:{}", JSON.toJSON(mergeRequest));
            if ("can_be_merged".equals(mergeRequest.getMergeStatus())) {
                gitlabApi.acceptMergeRequest(project, mergeRequest.getIid(), "通过合并");
            } else if ("unchecked".equals(mergeRequest.getMergeStatus())) {
                log.info("gitlabService mergeToMaster 无需合并代码 : " + JSON.toJSON(mergeRequest));
            } else if ("cannot_be_merged".equals(mergeRequest.getMergeStatus())) {
                throw new ServiceException(GitlabResponseCode.MERGE_ERROR_CANNOT_BE_MERGED);
            } else {
                log.info("gitlabService mergeToMaster 没有捕获到合并状态 : " + JSON.toJSON(mergeRequest));
            }
            log.info("gitlabService mergeToMaster 合并分支成功 projectUrl:{},sourceBranch:{},targetBranch:{}", projectUrl, sourceBranch, targetBranch);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.info("gitlabService 合并分支失败 e:{}", ExceptionUtils.getStackTrace(e));
            throw new ServiceException(GitlabResponseCode.MERGE_ERROR);
        }
    }

    /**
     * 合并分支
     * @param projectUrl
     * @param sourceBranch
     * @param targetBranch
     * @param title
     * @throws Exception
     */
    public void merge(String projectUrl, String sourceBranch, String targetBranch, String title) {
        log.info("GitlabService mergeToMaster 合并分支，项目git地址 projectUrl {},sourceBranch {},targetBranch {}", projectUrl, sourceBranch, targetBranch);
        if (projectUrl == null || sourceBranch == null || targetBranch == null) {
            throw new ServiceException(SysResponseCode.PARAM_ILLEGAL);
        }
        if (sourceBranch.equals(targetBranch)) {
            log.info("相同分支无需合并");
            return ;
        }
        GitlabProject project = this.getProject(projectUrl);
        if (project == null) {
            log.error("GitlabService mergeToMaster 没有找到git仓库，无法创建分支 ");
            throw new ServiceException(GitlabResponseCode.NO_GIT_REP);
        }
        try {
            log.info("GitlabService mergeToMaster 查询项目详情： project:{}", JSON.toJSON(project));
            if (hasMerged(project.getId(), sourceBranch, targetBranch)) {
                log.info("已合并代码，无需重复合并");
                return ;
            }
            this.createAndAcceptMergeRequest(project.getId(), sourceBranch, targetBranch, title);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.info("gitlabService 合并分支失败 e:{}", ExceptionUtils.getStackTrace(e));
            throw new ServiceException(GitlabResponseCode.MERGE_ERROR);
        }
    }

    /**
     * 创建并合并MR
     * @param projectId
     * @param sourceBranch
     * @param targetBranch
     * @param title
     * @throws Exception
     */
    private void createAndAcceptMergeRequest(Integer projectId, String sourceBranch, String targetBranch, String title) throws Exception {
        MergeRequest mergeRequest = this.createOrGetMergeRequest(projectId, sourceBranch, targetBranch, title);
        log.info("acceptMerge getMergeRequest {}", mergeRequest);
        if (mergeRequest == null) {
            throw new ServiceException(GitlabResponseCode.MERGE_ERROR_NOT_FIND_MERGED);
        }
        // 检查合并状态
        this.checkMergeStatus4Accept(projectId, mergeRequest);
        // 合并分支
        this.acceptMergeRequest(projectId, mergeRequest.getIid());
    }

    /**
     * 合并分支
     * @param projectId
     * @param requestId
     * @throws Exception
     */
    private void acceptMergeRequest(Integer projectId, Integer requestId) throws Exception {
        String url = baseUrl + projectId + "/merge_requests/" + requestId + "/merge";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPut httpPut = new HttpPut(url);
        httpPut.addHeader(GitlabConfig.HEADER_TOKEN_KEY, GitlabConfig.API_TOKEN);
        Map<String, Object> map = new HashMap<>();
        map.put("id", projectId);
        map.put("merge_request_iid", requestId);
        String jsonString = JSON.toJSONString(map);
        StringEntity entity = new StringEntity(jsonString, "UTF-8");
        httpPut.setEntity(entity);
        CloseableHttpResponse response = httpClient.execute(httpPut);
        log.info("合并分支返回的结果code {}", response.getStatusLine().getStatusCode());
        if (HTTP_OK == response.getStatusLine().getStatusCode()) {
            log.info("合并分支成功");
        } else if (HTTP_NOT_ACCEPTABLE == response.getStatusLine().getStatusCode()) {
            this.delMergeRequest(projectId, requestId);
            throw new ServiceException(GitlabResponseCode.MERGE_ERROR_CANNOT_BE_MERGED);
        } else {
            this.delMergeRequest(projectId, requestId);
            throw new ServiceException(GitlabResponseCode.MERGE_ERROR);
        }
    }

    /**
     * 检查分支状态
     * @param projectId
     * @param mergeRequest
     * @throws Exception
     */
    private void checkMergeStatus4Accept(Integer projectId, MergeRequest mergeRequest) throws Exception {
        // 有冲突合并失败
        if (GitMergeStatusEnum.CANNOT_BE_MERGED.getCode().equals(mergeRequest.getMergeStatus())) {
            this.delMergeRequest(projectId, mergeRequest.getIid());
            throw new ServiceException(GitlabResponseCode.MERGE_ERROR_CANNOT_BE_MERGED);
        }
        // 如果是unchecked状态，有可能是在异步执行中，尝试重试获取
        if (GitMergeStatusEnum.UNCHECKED.getCode().equals(mergeRequest.getMergeStatus())) {
            int retryCount = 0;
            while(++retryCount <= MAX_RETRY_COUNT) {
                mergeRequest = getMergeRequest(projectId, mergeRequest.getIid());
                if (GitMergeStatusEnum.CANNOT_BE_MERGED.getCode().equals(mergeRequest.getMergeStatus())) {
                    this.delMergeRequest(projectId, mergeRequest.getIid());
                    throw new ServiceException(GitlabResponseCode.MERGE_ERROR_CANNOT_BE_MERGED);
                } else if (GitMergeStatusEnum.CAN_BE_MERGED.getCode().equals(mergeRequest.getMergeStatus())) {
                    break;
                }
            }
        }
    }

    /**
     * 删除MR
     * @param projectId
     * @param requestId
     * @throws Exception
     */
    private void delMergeRequest(Integer projectId, Integer requestId) throws Exception {
        String url = baseUrl + projectId + "/merge_requests/" + requestId;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpDelete httpDelete = new HttpDelete(url);
        httpDelete.addHeader(GitlabConfig.HEADER_TOKEN_KEY, GitlabConfig.API_TOKEN);
        CloseableHttpResponse response = httpClient.execute(httpDelete);
        // code 204 删除成功
        int code = response.getStatusLine().getStatusCode();
        log.info("删除mergeRequest projectId {}, requestId {}, responseCode {}", projectId, requestId, code);

    }

    /**
     * 按照分支查找或者创建MR，如果已存在则直接返回，如果不存在则创建新的
     * @param projectId
     * @param sourceBranch
     * @param targetBranch
     * @param title
     * @return
     */
    private MergeRequest createOrGetMergeRequest(Integer projectId, String sourceBranch, String targetBranch, String title) throws Exception {
        MergeRequest mergeRequest = this.getOpenMergeRequest(projectId, sourceBranch, targetBranch);
        if (mergeRequest != null) {
            log.info("已存在mergeRequest,无需重复创建 mergeRequest {}", mergeRequest);
            return mergeRequest;
        }
        mergeRequest = this.createMergeRequest(projectId, sourceBranch, targetBranch, title);

        return mergeRequest;
    }

    /**
     * 创建mergeRequest
     * @param projectId
     * @param sourceBranch
     * @param targetBranch
     * @param title
     * @return
     * @throws Exception
     */
    private MergeRequest createMergeRequest(Integer projectId, String sourceBranch, String targetBranch, String title) throws Exception {
        log.info("去创建mergeRequest projectId {}, sourceBranch {}, targetBranch {}, title {}", projectId, sourceBranch, targetBranch, title);
        String url = baseUrl + projectId + "/merge_requests";
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader(GitlabConfig.HEADER_TOKEN_KEY, GitlabConfig.API_TOKEN);
        httpPost.setHeader("Content-Type", "application/json;charset=utf8");
        Map<String, Object> map = new HashMap<>();
        map.put("id", projectId);
        map.put("source_branch", sourceBranch);
        map.put("target_branch", targetBranch);
        map.put("title", title);
        String jsonString = JSON.toJSONString(map);
        StringEntity entity = new StringEntity(jsonString, StandardCharsets.UTF_8.displayName());
        httpPost.setEntity(entity);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = httpClient.execute(httpPost);
        if (response.getStatusLine().getStatusCode() == HTTP_CREATED) {
            String result = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.displayName());
            MergeRequest mergeRequest = JsonUtil.getObject(result, MergeRequest.class);
            log.info("创建mergeRequest成功 result {}", result);
            return mergeRequest;
        }
        log.error("创建mergeRequest失败");
        return null;
    }

    /**
     * 按分支查找MR
     * @param projectId
     * @param sourceBranch
     * @param targetBranch
     * @return
     * @throws Exception
     */
    private MergeRequest getOpenMergeRequest(Integer projectId, String sourceBranch, String targetBranch) throws Exception {
        String url = baseUrl + projectId + "/merge_requests?state=opened";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader(GitlabConfig.HEADER_TOKEN_KEY, GitlabConfig.API_TOKEN);
        CloseableHttpResponse response = httpClient.execute(httpGet);
        if (response.getStatusLine().getStatusCode() == HTTP_OK) {
            String result = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.displayName());
            List<MergeRequest> mergeRequests = JsonListUtil.jsonToList(result, MergeRequest.class);
            if (mergeRequests == null || mergeRequests.size() < 1) {
                return null;
            }
            for (MergeRequest mergeRequest : mergeRequests) {
                if (mergeRequest.getSourceBranch().equals(sourceBranch) && mergeRequest.getTargetBranch().equals(targetBranch)) {
                    return mergeRequest;
                }
            }
        }
        return null;
    }

    private MergeRequest getMergeRequest(Integer projectId, Integer requestId) throws Exception {
        String url = baseUrl + projectId + "/merge_requests/" + requestId;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader(GitlabConfig.HEADER_TOKEN_KEY, GitlabConfig.API_TOKEN);
        CloseableHttpResponse response = httpClient.execute(httpGet);
        if (response.getStatusLine().getStatusCode() == HTTP_OK) {
            String result = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.displayName());
            MergeRequest mergeRequest = JsonUtil.getObject(result, MergeRequest.class);
            return mergeRequest;
        }
        return null;
    }

    /**
     * 判断分支是否已经合并
     * @param projectId
     * @param sourceBranch
     * @param targetBranch
     * @return
     * @throws Exception
     */
    private boolean hasMerged(Integer projectId, String sourceBranch, String targetBranch) throws Exception {
        Commit sourceLastCommit = getLastCommit(projectId, sourceBranch);
        for (int pageNum = 1; pageNum < MAX_QUERY_GIT_COMMIT_PAGE_COUNT; pageNum++) {
            Pagination page = new Pagination();
            page.setPage(pageNum);
            List<GitlabCommit> targetCommitList = gitlabApi.getCommits(projectId, page, targetBranch);
            for (GitlabCommit targetCommit : targetCommitList) {
                if (sourceLastCommit.getId().equals(targetCommit.getId())) {
                    log.info("hasMerged 判断是否已经合并，已合并 sourceBranch {}, targetBranch {}", sourceBranch, targetBranch);
                    return true;
                }
            }
        }
        log.info("hasMerged 判断是否已经合并，只获取最后20条合并记录，并未发现合并 sourceBranch {}, targetBranch {}", sourceBranch, targetBranch);
        return false;
    }

    /**
     * 获取某分支的最后一条提交记录
     * @param projectId
     * @param branchName
     * @return
     * @throws Exception
     */
    private Commit getLastCommit(Integer projectId, String branchName) throws Exception {
        List<Commit> listCommits = listCommits(projectId, branchName);
        if (listCommits == null || listCommits.size() < 1) {
            return null;
        }
        Commit lastCommit = listCommits.get(0);
        log.info("getLastCommit 获取最后一条提交记录 branchName {},commit {}", branchName, lastCommit);
        return lastCommit;
    }

    /**
     * 获取某分支的提交记录，最多展示20行
     * @param projectId
     * @param branchName
     * @return
     * @throws Exception
     */
    private List<Commit> listCommits(Integer projectId, String branchName) throws Exception {
        String url = baseUrl + projectId + "/repository/commits?ref_name=" + branchName;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader(GitlabConfig.HEADER_TOKEN_KEY, GitlabConfig.API_TOKEN);
        CloseableHttpResponse response = httpClient.execute(httpGet);
        if (response.getStatusLine().getStatusCode() == HTTP_OK) {
            String result = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.displayName());
            List<Commit> commits = JsonListUtil.jsonToList(result, Commit.class);
            return commits;
        }
        return null;
    }

    private HttpEntity<String> getHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(GitlabConfig.HEADER_TOKEN_KEY, GitlabConfig.API_TOKEN);
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
        return requestEntity;
    }

    /**
     * 获取所有分支
     * @param projectUrl
     */
    public List<String> getAllBranch(String projectUrl) {
        log.info("GitlabService getAllBranch 获取所有分支，项目git地址 projectUrl:{}", projectUrl);
        if (projectUrl == null) {
            throw new ServiceException(SysResponseCode.PARAM_ILLEGAL);
        }
        try {
            GitlabProject project = this.getProject(projectUrl);
            if (project == null) {
                log.error("GitlabService getAllBranch 没有找到git仓库 projectUrl:{}", projectUrl);
                throw new ServiceException(GitlabResponseCode.NO_GIT_REP);
            }
            List<String> branchNameList = Lists.newArrayList();
            List<GitlabBranch> branchList = gitlabApi.getBranches(project);

            if (branchList != null) {
                for (GitlabBranch branch : branchList) {
                    branchNameList.add(branch.getName());
                }
            }
            return branchNameList;
        } catch (Exception e) {
            log.error("GitlabService getAllBranch 没有找到git仓库 projectUrl:{}", projectUrl);
            throw new ServiceException(GitlabResponseCode.NO_GIT_REP);
        }
    }

    /**
     * 判断是否存在分支
     * @param projectUrl
     * @param branchName
     * @return
     */
    public boolean containsBranchName(String projectUrl, String branchName) {
        long startTime = System.currentTimeMillis();
        log.info("GitlabService containsBranchName 查询是否拥有此分支，项目git地址 projectUrl:{},branchName:{}", projectUrl, branchName);
        if (projectUrl == null) {
            throw new ServiceException(SysResponseCode.PARAM_ILLEGAL);
        }
        try {
            GitlabProject project = this.getProject(projectUrl);
            if (project == null) {
                log.error("GitlabService containsBranchName 查询是否拥有此分支 projectUrl:{}", projectUrl);
                throw new ServiceException(GitlabResponseCode.NO_GIT_REP);
            }
            GitlabBranch gitlabBranch = gitlabApi.getBranch(project, branchName);
            log.info("GitlabService containsBranchName 查询是否拥有此分支,耗时:{}", System.currentTimeMillis() - startTime);
            if (gitlabBranch != null) {
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("GitlabService getAllBranch 没有找到git仓库 projectUrl:{}", projectUrl);
            throw new ServiceException(GitlabResponseCode.NO_GIT_REP);
        }
    }

    /**
     * 获取所有GitLab的group分组信息
     * @return
     */
    public GetAllGroupResponseDTO getAllGroup() {
        // 返回结果
        GetAllGroupResponseDTO responseDTO = new GetAllGroupResponseDTO();
        responseDTO.setGroupNameList(Lists.newArrayList());
        // 调用GitLab开放接口
        List<GitlabGroup> groups;
        try {
            groups = gitlabApi.getGroups();
        } catch (IOException e) {
            log.error("获取所有GitLab的group分组信息时发生异常，异常信息：{}", ExceptionUtils.getStackTrace(e));
            return responseDTO;
        }
        if (CollectionUtils.isEmpty(groups)) {
            return responseDTO;
        }
        log.info("共获取所有GitLab的group分组条数 => {}", groups.size());
        List<String> groupNameList = groups.stream().filter(Objects::nonNull)
            .map(GitlabGroup::getFullPath)
            .collect(Collectors.toList());
        responseDTO.setGroupNameList(groupNameList);
        return responseDTO;
    }

    /**
     * 根据GitLab的group分组创建工程Project
     * @param requestDTO
     * @return
     */
    public boolean createProjectByGroup(CreateProjectByGroupRequestDTO requestDTO) {
        try {
            @NotBlank(message = "Gitlab分组名称不能为空") String groupName = requestDTO.getGroupName();
            @NotBlank(message = "Gitlab项目名称不能为空") String projectName = requestDTO.getProjectName();
            // 获取GitLab分组信息
            GitlabGroup group = gitlabApi.getGroup(groupName);
            // 在分组下创建项目
            GitlabProject projectForGroup = gitlabApi.createProjectForGroup(projectName, group);
            log.info("GitlabService createProjectByGroup result: {}", JSON.toJSONString(projectForGroup));
            // 赋予人员工程权限
            boolean assignPermissionFlag = this.assignPermission4Project(projectName, projectForGroup);
            if (assignPermissionFlag) {
                log.info("赋予业务ownerGitLab工程Mater权限成功");
            } else {
                log.error("赋予业务ownerGitLab工程Mater权限失败");
            }
            return true;
        } catch (Exception e) {
            log.error("GitlabService createProjectByGroup occur error:{}", ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    /**
     * 赋予人员工程权限
     * @param projectName
     * @param projectForGroup
     * @return
     */
    private boolean assignPermission4Project(@NotBlank(message = "Gitlab项目名称不能为空") String projectName, GitlabProject projectForGroup) {
        if (Objects.isNull(projectForGroup)) {
            return false;
        }
        String powerPermissionUserName;
        // 根据AppCode查询录入时对应的应用owner
        ResponseData<AppListResponse> applicationInfo = milkywayClient.getApplicationInfo(projectName);
        if (applicationInfo.isSuccess() && Objects.nonNull(applicationInfo.getData())) {
            AppListResponse data = applicationInfo.getData();
            powerPermissionUserName = data.getOwnerName();
            log.info("远程调用银河应用根据AppCode没有查询到对应的应用owner信息:{}", powerPermissionUserName);
        } else {
            log.warn("远程调用银河应用根据AppCode没有查询到对应的应用owner信息时返回状态非成功");
            return false;
        }
        if (StringUtils.isBlank(powerPermissionUserName)) {
            log.warn("远程调用银河应用返回结果中没有相应的人员信息");
            return false;
        }
        String pinyinName = HanyupinyinUtil.getPinyinString(powerPermissionUserName);
        // GitlabProject project, GitlabUser user, GitlabAccessLevel accessLevel
        GitlabAccessLevel gitlabAccessLevel;
        // owner master权限
        gitlabAccessLevel = GitlabAccessLevel.Master;
        try {
            GitlabUser gitlabUser = gitlabApi.getUserViaSudo(pinyinName);
            GitlabProjectMember projectMember = gitlabApi.addProjectMember(projectForGroup, gitlabUser, gitlabAccessLevel);
            log.info("GitLab应用赋予权限，返回结果：{}", JSON.toJSONString(projectMember));
            return true;
        } catch (IOException e) {
            log.error("GitLab应用赋予权限，发生异常：{}", ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    /**
     * 根据GitLab的namespace和项目名称查询工程
     * @param requestDTO
     */
    public GetProjectByNamespaceAndProjectNameResponseDTO getProjectByNamespaceAndProjectName(GetProjectByNamespaceAndProjectNameRequestDTO requestDTO) {
        GetProjectByNamespaceAndProjectNameResponseDTO responseDTO = new GetProjectByNamespaceAndProjectNameResponseDTO();
        try {
            String namespace = requestDTO.getNamespace();
            String projectName = requestDTO.getProjectName();
            GitlabProject project = gitlabApi.getProject(namespace, projectName);
            log.info("GitlabService getProjectByNamespaceAndProjectName result: {}", JSON.toJSONString(project));
            responseDTO.setSshUrl(project.getSshUrl());
            return responseDTO;
        } catch (IOException e) {
            log.error("GitlabService getProjectByNamespaceAndProjectName occur error:{}", ExceptionUtils.getStackTrace(e));
            return responseDTO;
        }
    }

    /**
     * 监听创建应用的消息来创建gitlab
     * @param appCode
     * @return
     */
    public boolean createGitLab(String appCode) {
        if (StringUtils.isBlank(appCode)) {
            log.error("监听创建应用的消息来创建gitlab时入参应用编码为空");
            return false;
        }
        GetInfoByAppCodeRequestDTO requestDTO = new GetInfoByAppCodeRequestDTO();
        requestDTO.setAppCode(appCode);
        ResponseData<GetInfoByAppCodeResponseDTO> responseData = milkywayClient.getDomainArchitectureInfoByAppCode(token, requestDTO);
        String gitLabGroupName = "";
        if (responseData.isSuccess()) {
            GetInfoByAppCodeResponseDTO data = responseData.getData();
            log.info("根据应用code查询到架构域信息：{}", JSON.toJSONString(data));
            gitLabGroupName = data.getGitLabGroupName();
        } else {
            log.error("根据应用code没有查询到架构域信息");
            return false;
        }
        if (StringUtils.isBlank(gitLabGroupName)) {
            log.error("根据应用编码没有查询到gitlab分组信息");
        }
        // 创建gitLab账号
        CreateProjectByGroupRequestDTO groupRequestDTO = new CreateProjectByGroupRequestDTO();
        groupRequestDTO.setGroupName(gitLabGroupName);
        groupRequestDTO.setProjectName(appCode);
        return this.createProjectByGroup(groupRequestDTO);
    }
}
