package com.choice.cloud.architect.groot.gitlab;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.choice.cloud.architect.groot.dto.apollo.ApolloQueryAllItemByEnvIdcNamespaceResponseDTO;
import com.choice.cloud.architect.groot.exception.ServiceException;
import com.choice.cloud.architect.groot.gitlab.dto.Commit;
import com.choice.cloud.architect.groot.gitlab.dto.MergeRequest;
import com.choice.cloud.architect.groot.response.code.GitlabResponseCode;
import com.choice.cloud.architect.groot.response.code.SysResponseCode;
import com.choice.cloud.architect.groot.util.JsonListUtil;
import com.choice.cloud.architect.groot.util.JsonUtil;
import com.google.common.collect.Maps;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.models.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * @ClassName TestGitService
 * @Description TODO
 * @Author Guangshan Wang
 * @Date 2020/4/27/027 23:41
 */
public class TestGitService {

    private static  GitlabAPI gitlabApi = GitlabAPI.connect(GitlabConfig.HOST_URL, GitlabConfig.API_TOKEN);

    public static void main(String[] args) throws Exception {

        String projectUri = "git@gitlab.choicesoft.com.cn:choice-scm/choice-scm.git";
        GitlabProject project = getProject(projectUri);


        String fromBranch = "master";
        String toBranch = "mq-gray-plugin";

//        getOpenMergeRequest(project.getId(), fromBranch, toBranch);

//        createMergeRequest(project.getId(), fromBranch, toBranch, "123");
        getMergeRequest1(project.getId(), 315);
        delMergeRequest(project.getId(), 315);

//        listCommits(project.getId(), fromBranch);

//        getMergeRequest(project.getId(), 335);
//        acceptMergeRequest(project.getId(), 335);
//
//
//        if (hasMerged(project.getId(), fromBranch, toBranch)) {
//            return;
//        }
//
//        JSONObject compareRes = compareBranch(project.getId(), fromBranch, toBranch);
//        if (compareRes.getBoolean("compare_timeout") || compareRes.getJSONArray("diffs").size() == 0) {
//            return ;
//        }
//
//        JSONObject createMergeRes = createMergeRequest(project.getId(), fromBranch, toBranch, "测试合并分支");
//
//        boolean canMerge = false;
//        int index = 0;
//        while(!canMerge) {
//            JSONObject getMergeRes = getMergeRequest(project.getId(), createMergeRes.getInteger("iid"));
//            getMergeRes.getString("web_url");
//            if ("can_be_merged".equals(getMergeRes.getString("merge_status"))) {
//                canMerge = true;
//            }
//            getMergeRequestChanges(project.getId(), createMergeRes.getInteger("iid"));
//            System.out.println("第 " + index++ + " 次查看是否可以合并 canMerge：" + canMerge);
//            if (index > 10) {
//                break;
//            }
//        }
//        acceptMergeRequest(project.getId(), createMergeRes.getInteger("iid"));
//
//        List<GitlabMergeRequest> mergeRequestList = gitlabApi.getOpenMergeRequests(project);
//        for (GitlabMergeRequest request : mergeRequestList) {
//            System.out.println("mergeRequest: " + JSON.toJSON(request));
//            System.out.println("mergeRequest status: " + request.getMergeStatus() + ", " + );
//            compareBranch(project.getId(), request.getSourceBranch(), request.getTargetBranch());
//        }
//        gitlabApi.protectBranch(project, "app");
//        gitlabApi.unprotectBranch(project, "app");
//        GitlabBranch appBranch = gitlabApi.getBranch(project, "app");
//
//        GitlabBranch masterBranch = gitlabApi.getBranch(project, "master");
//
//        System.out.println(JSON.toJSON(appBranch));
//        System.out.println(JSON.toJSON(masterBranch));
//
//        GitlabCommitComparison comparison = gitlabApi.compareCommits(project.getId(), "c1bd4c90fd208c5562d18c618b0b8693104cab1a", "332e5ef0a25cc569988f83aa8a87b541fe9dfeac");
//        System.out.println(JSON.toJSON(comparison));
//
//        merge(projectUri, "master", "20200502-git-merge-02");
    }

    private static RestTemplate restTemplate = new RestTemplate();
    private static String baseUrl = "http://gitlab.choicesoft.com.cn/api/v4/projects/";

    private static void getOpenMergeRequest(Integer projectId, String sourceBranch, String targetBranch) throws Exception {
        String url = baseUrl + projectId + "/merge_requests?state=opened";

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader(GitlabConfig.HEADER_TOKEN_KEY, GitlabConfig.API_TOKEN);
        CloseableHttpResponse response = httpClient.execute(httpGet);
        if (response.getStatusLine().getStatusCode() == HTTP_OK) {
            String result = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.displayName());
            List<MergeRequest> mergeRequests = JsonListUtil.jsonToList(result, MergeRequest.class);
            System.out.println(result);
        }
    }

    private static JSONArray listOpenMergeRequest(Integer projectId) {
        String url = baseUrl + projectId + "/merge_requests?state=opened";
        ResponseEntity<JSONArray> resEntity = restTemplate.exchange(url, HttpMethod.GET, getHeader(), JSONArray.class);
        JSONArray responseBody = resEntity.getBody();
        System.err.println("listOpenMergeRequest: " + responseBody);
        return responseBody;
    }

    private static JSONObject getMergeRequest(Integer projectId, Integer requestId) {
        String url = baseUrl + projectId + "/merge_requests/" + requestId;
        ResponseEntity<JSONObject> resEntity = restTemplate.exchange(url, HttpMethod.GET, getHeader(), JSONObject.class);
        JSONObject responseBody = resEntity.getBody();
        System.err.println("getMergeRequest: " + responseBody);
        System.err.println("源分支：" + responseBody.getString("source_branch") + " 目标分支：" + responseBody.getString("target_branch"));
        return responseBody;
    }

    private static JSONObject getMergeRequest1(Integer projectId, Integer requestId) throws Exception {
        String url = baseUrl + projectId + "/merge_requests/" + requestId;

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader(GitlabConfig.HEADER_TOKEN_KEY, GitlabConfig.API_TOKEN);
        CloseableHttpResponse response = httpClient.execute(httpGet);
        if (response.getStatusLine().getStatusCode() == HTTP_OK) {
            String result = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.displayName());
            MergeRequest mergeRequests = JsonUtil.getObject(result, MergeRequest.class);
            System.out.println(mergeRequests);
        }
       return null;
    }

    private static void delMergeRequest(Integer projectId, Integer requestId) throws Exception {
        String url = baseUrl + projectId + "/merge_requests/" + requestId;

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpDelete httpDelete = new HttpDelete(url);
        httpDelete.addHeader(GitlabConfig.HEADER_TOKEN_KEY, GitlabConfig.API_TOKEN);
        CloseableHttpResponse response = httpClient.execute(httpDelete);
        int code = response.getStatusLine().getStatusCode();
        System.out.println("code : " + code);
    }

    private static JSONObject getMergeRequestChanges(Integer projectId, Integer requestId) {
        String url = baseUrl + projectId + "/merge_requests/" + requestId + "/changes";
        ResponseEntity<JSONObject> resEntity = restTemplate.exchange(url, HttpMethod.GET, getHeader(), JSONObject.class);
        JSONObject responseBody = resEntity.getBody();
        System.err.println("getMergeRequestChanges: " + responseBody);
        System.err.println("源分支：" + responseBody.getString("source_branch") + " 目标分支：" + responseBody.getString("target_branch"));
        return responseBody;
    }

    private static JSONObject acceptMergeRequest(Integer projectId, Integer requestId) {
        String url = baseUrl + projectId + "/merge_requests/" + requestId + "/merge";
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPut httpPut = new HttpPut(url);
            httpPut.addHeader("PRIVATE-TOKEN", "zf7yxxVjcx_7gqyEDjZz");
            Map<String, Object> map = new HashMap<>();
            map.put("id", projectId);
            map.put("merge_request_iid", requestId);
            // 将Object转换为json字符串;
            String jsonString = JSON.toJSONString(map);
            StringEntity entity = new StringEntity(jsonString, "UTF-8");
            httpPut.setEntity(entity);

            System.out.println("Executing request " + httpPut.getRequestLine());

            CloseableHttpResponse response = httpClient.execute(httpPut);
            System.out.println(response);
            HttpEntity resBody = (HttpEntity) response.getEntity();
            int code = response.getStatusLine().getStatusCode();
            System.out.println(code);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
        return null;
    }

    private static JSONObject compareBranch(Integer projectId, String fromBranch, String toBranch) {
        String url = baseUrl + projectId + "/repository/compare?from=" + fromBranch + "&to=" + toBranch;
        ResponseEntity<JSONObject> resEntity = restTemplate.exchange(url, HttpMethod.GET, getHeader(), JSONObject.class);
        JSONObject responseBody = resEntity.getBody();
        System.err.println("compare fromBranch: " + fromBranch + ",toBranch: " + toBranch);
        System.err.println("compare: " + responseBody);
        return responseBody;
    }

    private static JSONObject createMergeRequest(Integer projectId, String sourceBranch, String targetBranch, String title) {
        String url = baseUrl + projectId + "/merge_requests";
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("PRIVATE-TOKEN", "zf7yxxVjcx_7gqyEDjZz");
            httpPost.setHeader("Content-Type", "application/json;charset=utf8");
            Map<String, Object> map = new HashMap<>();
            map.put("id", projectId);
            map.put("source_branch", sourceBranch);
            map.put("target_branch", targetBranch);
            map.put("title", title);
            String jsonString = JSON.toJSONString(map);
            StringEntity entity = new StringEntity(jsonString, StandardCharsets.UTF_8.displayName());
            httpPost.setEntity(entity);

            CloseableHttpResponse response = httpClient.execute(httpPost);
            System.out.println("response : " + response);
            int code = response.getStatusLine().getStatusCode();
            System.out.println(code);

            String result = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.displayName());
            System.out.println(result);
            MergeRequest mergeRequestList = JsonUtil.getObject(result, MergeRequest.class);
            System.out.println(mergeRequestList);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }

        return null;
    }
//
    private static JSONObject createMergeRequest1(Integer projectId, String sourceBranch, String targetBranch, String title) {
        String url = baseUrl + projectId + "/merge_requests";
        MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<String, Object>();
        requestBody.add("id", projectId);
        requestBody.add("source_branch", sourceBranch);
        requestBody.add("target_branch", targetBranch);
        requestBody.add("title", title);
        HttpHeaders headers = new HttpHeaders();
        headers.add("PRIVATE-TOKEN", "zf7yxxVjcx_7gqyEDjZz");
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<MultiValueMap<String, Object>>(requestBody, headers);
        ResponseEntity<String> resEntity = restTemplate.postForEntity(url, httpEntity, String.class);
        JSONObject responseBody = JSONObject.parseObject(resEntity.getBody());
        System.err.println("createMergeRequest sourceBranch: " + sourceBranch + ",targetBranch: " + targetBranch);
        System.err.println("createMergeRequest responseBody: " + responseBody);
        return responseBody;
    }


    private static JSONObject getSingleCommit(Integer projectId, String sha) {
        String url = baseUrl + projectId + "/repository/commits/" + sha;
        ResponseEntity<JSONObject> resEntity = restTemplate.exchange(url, HttpMethod.GET, getHeader(), JSONObject.class);
        JSONObject responseBody = resEntity.getBody();
        System.err.println("getSingleCommit sha: " + sha);
        System.err.println("getSingleCommit: " + responseBody);
        return responseBody;
    }

    private static JSONArray listCommits(Integer projectId, String branchName, String sinceTime) {
        String url = baseUrl + projectId + "/repository/commits?ref_name=" + branchName;
        if (sinceTime != null) {
            url += "&since=" + sinceTime + "&order=topo";
        }

        ResponseEntity<JSONArray> resEntity = restTemplate.exchange(url, HttpMethod.GET, getHeader(), JSONArray.class);
        JSONArray responseBody = resEntity.getBody();
        System.err.println("listCommits branchName: " + branchName + "总共提交次数：" + responseBody.size());
        System.err.println("listCommits: " + responseBody);
        return responseBody;
    }

    private static List<Commit> listCommits(Integer projectId, String branchName) {
        String url = baseUrl + projectId + "/repository/commits?ref_name=" + branchName;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("PRIVATE-TOKEN", "zf7yxxVjcx_7gqyEDjZz");
        System.out.println("Executing request " + httpGet.getRequestLine());
        try {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            int code = response.getStatusLine().getStatusCode();
            String result = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.displayName());
            List<Commit> commits = JsonListUtil.jsonToList(result, Commit.class);
            System.out.println(commits);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    private static JSONObject getLastCommit(Integer projectId, String branchName) {
        JSONArray listCommitsRes = listCommits(projectId, branchName, null);
        JSONObject lastCommit = listCommitsRes.getJSONObject(0);
        String lastCommitId = lastCommit.getString("id");
        System.err.println("getLastCommit branchName: " + branchName + ", lastCommitId:" + lastCommitId);
        return lastCommit;
    }

    private static boolean hasMerged(Integer projectId, String sourceBranch, String targetBranch) {
        JSONObject lastCommit = getLastCommit(projectId, sourceBranch);
        String lastCommitId = lastCommit.getString("id");
        String lastCommitTime = lastCommit.getString("created_at");
        lastCommitTime = lastCommitTime.substring(0, lastCommitTime.length() - 10);
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date lastCommitDate = sdf.parse(lastCommitTime);
            Calendar calendar = Calendar.getInstance(); //得到日历
            calendar.setTime(lastCommitDate);
            calendar.add(Calendar.DAY_OF_MONTH, -180);
            lastCommitTime = sdf.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONArray targetBranchCommits = listCommits(projectId, targetBranch, lastCommitTime);
        for (int i = 0; i < targetBranchCommits.size(); i++) {
            JSONObject targetCommit = targetBranchCommits.getJSONObject(i);
            System.err.println("hasMerged lastCommit:" + lastCommitId + ",targetCommit:" + targetCommit.getString("id"));
            if (lastCommitId.equals(targetCommit.getString("id"))) {
                System.err.println("hasMerged 判断是否已合并 sourceBranch:" + sourceBranch + ",targetBranch:" + targetBranch + ",ret:" + true);
                return true;
            }
        }
        System.err.println("hasMerged 判断是否已合并 sourceBranch:" + sourceBranch + ",targetBranch:" + targetBranch + ",ret:" + false);
        return false;
    }


    private static HttpEntity<String> getHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("PRIVATE-TOKEN", "zf7yxxVjcx_7gqyEDjZz");
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
        return requestEntity;
    }

    private static GitlabProject getProject(String projectUrl) {
        if (projectUrl == null) {
            throw new ServiceException(SysResponseCode.PARAM_ILLEGAL);
        }
        try {
            String urlFirstSplit[] = projectUrl.split("/");
            String gitName = urlFirstSplit[1];
            String projectName = gitName.substring(0, gitName.length() - ".git".length());
            String urlSecondSplit[] = urlFirstSplit[0].split(":");
            String namespace = urlSecondSplit[1];
            GitlabProject project = gitlabApi.getProject(namespace, projectName);
            return project;
        } catch (Exception e) {
            throw new ServiceException(GitlabResponseCode.NO_GIT_REP);
        }
    }

    /**
     * 合并到master
     * @param projectUrl
     */
    public static void merge(String projectUrl, String sourceBranch, String targetBranch) {
        if (projectUrl == null || sourceBranch == null || targetBranch == null) {
            throw new ServiceException(SysResponseCode.PARAM_ILLEGAL);
        }

        try {
            GitlabProject project = getProject(projectUrl);
            if (project == null) {
                throw new ServiceException(GitlabResponseCode.NO_GIT_REP);
            }
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
                mergeRequest = gitlabApi.createMergeRequest(project.getId(), sourceBranch, targetBranch, GitlabConfig.USER_ID, "测试合并请求");
            }

            System.out.println(mergeRequest);

            if ("can_be_merged".equals(mergeRequest.getMergeStatus())) {
                gitlabApi.acceptMergeRequest(project, mergeRequest.getIid(), "通过测试合并");
            } else if ("unchecked".equals(mergeRequest.getMergeStatus())) {
                System.out.println("无需合并代码 : " + JSON.toJSON(mergeRequest));
            } else if ("cannot_be_merged".equals(mergeRequest.getMergeStatus())) {
                throw new ServiceException(GitlabResponseCode.MERGE_ERROR_CANNOT_BE_MERGED);
            } else {
                System.out.println("没有捕获到合并状态 : " + JSON.toJSON(mergeRequest));
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException(GitlabResponseCode.MERGE_ERROR);
        }
    }
}
