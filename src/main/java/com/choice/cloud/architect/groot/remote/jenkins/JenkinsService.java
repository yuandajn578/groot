package com.choice.cloud.architect.groot.remote.jenkins;

import com.choice.cloud.architect.groot.dto.JobBuildNodeDTO;
import com.choice.cloud.architect.groot.exception.ServiceException;
import com.choice.cloud.architect.groot.response.code.PublishResponseCode;
import com.choice.cloud.architect.groot.support.PollingHandler;
import com.google.common.collect.Lists;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildWithDetails;
import com.offbytwo.jenkins.model.JobWithDetails;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class JenkinsService {

    @Autowired
    private JenkinsServer jenkinsServer;

    @Autowired
    private JenkinsFeignClient jenkinsFeignClient;


    public void createJobFrom(String jobName, String fromJobName) {
        try {
            String fromJobXml = jenkinsServer.getJobXml(fromJobName);
            jenkinsServer.createJob(jobName, fromJobXml, false);//jobXml  copy job

        } catch (IOException e) {
            log.warn("copy job warning");
        }
    }

    public void copyJob(String jobName, String fromJobName) {
        jenkinsFeignClient.copyJobItem(jobName, "copy", fromJobName);
    }

    public int buildJob(String jobName, Map<String, String> param) {
        JobWithDetails job;

        try {
            job = jenkinsServer.getJob(jobName);
        } catch (IOException e) {
            job = null;
        }

        if (job == null) {
            throw new ServiceException("jenkins job 获取失败");
        }

        return buildJob(job, param);
    }
    public int buildJob(JobWithDetails job, Map<String, String> param) {
        int jobBuildNumber;
        try {
            jobBuildNumber = job.getNextBuildNumber();
            job.build(param);
        } catch (IOException e) {
            throw new ServiceException("jenkins构建无响应");
        }

        return jobBuildNumber;
    }


    public List<Build> getJobBuilds(String jobName){
        try {
            JobWithDetails job = jenkinsServer.getJob(jobName);
            return job.getBuilds();
        } catch (IOException e) {
            throw new ServiceException("get job builds fail");
        }
    }


    public Build getBuild(String jobName, int num) {
        try {
            return new PollingHandler<Build>().process(
                    10, 2, () -> {
                        JobWithDetails loopJob = getJobInfoByName(jobName);
                        return loopJob.getBuildByNumber(num);
                    }
            );
        } catch (Exception e) {
            log.warn("Build-> jobName:{}, num:{}  not found", jobName, num);
            throw new ServiceException(PublishResponseCode.JENKINS_BUILD_NOT_FOUND);
        }
    }

    public String stopBuildByNumber(String jobName, int num) {
        try {
            JobWithDetails job = jenkinsServer.getJob(jobName);
            Build build = job.getBuildByNumber(num);
            if (build != null) {
                return build.Stop();
            }
            return null;
        } catch (IOException e) {
            throw new ServiceException("get build fail");
        }
    }

    public Build getLastBuild(String jobName) {
        try {
            JobWithDetails job = jenkinsServer.getJob(jobName);
            return job.getLastBuild();
        } catch (IOException e) {
            throw new ServiceException("get last build fail");
        }
    }

    public JobWithDetails getJobInfoByName(String jobName) {
        try {
            return jenkinsServer.getJob(jobName);
        } catch (IOException e) {
            return null;
        }

    }

    public String getConsoleOutputText(String jobName, int buildNumber){

        try {
            JobWithDetails job = jenkinsServer.getJob(jobName);
            Build build = job.getBuildByNumber(buildNumber);
            BuildWithDetails buildWithDetails = build.details();
            return buildWithDetails.getConsoleOutputText();
        } catch (IOException e) {
            throw new ServiceException("get console log fail");
        }

    }

    public String getNodeLogs(String name, int num, int node) {
        return jenkinsFeignClient.getBuildNodeLogs(name, num, node);
    }


    public void enableJob(String jobName) {
        try {
            jenkinsServer.enableJob(jobName);
        } catch (IOException e) {
            throw new ServiceException("enable job fail");
        }
    }


    public void disableJob(String jobName) {
        try {
            jenkinsServer.disableJob(jobName);
        } catch (IOException e) {
            throw new ServiceException("disable job fail");
        }
    }

    public void deleteJob(String jobName) {
        try {
            jenkinsServer.deleteJob(jobName);
        } catch (IOException e) {
            throw new ServiceException("delete job fail");
        }
    }

    public List<JobBuildNodeDTO> getBuildNodes(String jobName, int buildNum) {
        List<Map<String, Object>> resData = jenkinsFeignClient.getJobBuildNodes(jobName, buildNum);
        List<JobBuildNodeDTO> nodeDTOList = Lists.newArrayList();
        if(CollectionUtils.isNotEmpty(resData)) {
            for (Map<String, Object> map: resData) {
                JobBuildNodeDTO jobBuildNodeDTO = new JobBuildNodeDTO();
                jobBuildNodeDTO.setDisplayName((String) map.get("displayName"));
                jobBuildNodeDTO.setDurationInMillis((Integer) map.get("durationInMillis"));
                jobBuildNodeDTO.setId((String) map.get("id"));
                jobBuildNodeDTO.setResult((String) map.get("result"));
                jobBuildNodeDTO.setStartTime((String) map.get("startTime"));
                jobBuildNodeDTO.setState((String) map.get("state"));
                jobBuildNodeDTO.setType((String) map.get("type"));
                nodeDTOList.add(jobBuildNodeDTO);
            }
        }

        return nodeDTOList;
    }
}
