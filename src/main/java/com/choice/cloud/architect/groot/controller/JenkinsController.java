package com.choice.cloud.architect.groot.controller;

import com.choice.cloud.architect.groot.remote.jenkins.JenkinsService;
import com.choice.cloud.architect.groot.response.ResponseData;
import com.choice.cloud.architect.groot.service.inner.PublishBlackListService;
import com.google.common.collect.Maps;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.JobWithDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/jk")
public class JenkinsController {

//    @Autowired
//    private JenkinsService jenkinsService;
    @Autowired
    private JenkinsServer jenkinsServer;

    @GetMapping
    public ResponseData<Boolean> testPublishBlack() {
        return ResponseData.createBySuccess();
    }
//
//    public ResponseData<String> getJobBuilds(@RequestParam String jobName){
//        JobWithDetails jobWithDetails = jenkinsService.getJobInfoByName(jobName);
//        if (null == jobWithDetails) {
//            return ResponseData.createBySuccess("job不存在！");
//        }
//        return ResponseData.createBySuccess(jobWithDetails.getDisplayName());
//    }
//
//    public ResponseData<String> getConsoleLog(@RequestParam String jobName, @RequestParam int number){
//        return ResponseData.createBySuccess(jenkinsService.getConsoleOutputText(jobName, number));
//    }
//
//    public ResponseData enableJob(@RequestParam String jobName) {
//        jenkinsService.enableJob(jobName);
//        return ResponseData.createBySuccess();
//    }

//    public ResponseData<Integer> build(@RequestParam String jobName) {
//        Map<String, String> param = Maps.newHashMap();
//        param.put("cluster", "offline");
//        param.put("namespace", "dev");
//        param.put("env_type", "dev");
//        param.put("appname", "cicd-test");
//        param.put("repo", "gitlab@gitlab.choicesoft.com.cn:wudalong/cicd-test.gitlab");
//        param.put("branch", "master");
//        param.put("apollo_env", "DEV");
//        param.put("apollo_idc", "default");
//        param.put("healthcheck_path", "/ping");
//        param.put("healthcheck_initialDelaySeconds", "160");
//        param.put("healthcheck_periodSeconds", "3");
//        param.put("healthcheck_successThreshold", "1");
//        param.put("healthcheck_failureThreshold", "2");
//        param.put("healthcheck_timeoutSeconds", "2");
//        param.put("healthcheck_port", "8080");
//        param.put("sonar", "1");
//        param.put("sonar_path", "./");
//        param.put("service_group", "stable"); //envCode
//        param.put("mq_gray_switch", "off");
//        param.put("framework", "maven");
//        return ResponseData.createBySuccess(jenkinsService.buildJob(jobName, param));
//    }

}
