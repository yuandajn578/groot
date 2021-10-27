package com.choice.cloud.architect.groot.remote.jenkins;

import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(value = "jenkinsFeignClient", url = "${jenkins.openapi.url}")
public interface JenkinsFeignClient {

    /**
     * 复制job
     * @param name
     * @param mode
     * @param from
     * @return
     */
    @PostMapping("createItem")
    Response copyJobItem(
            @RequestParam("name") String name,
            @RequestParam("mode") String mode,
            @RequestParam("from") String from
    );


    /**
     * 获取node日志
     * @param name
     * @param num
     * @param node
     * @return
     */
    @GetMapping("blue/rest/organizations/jenkins/pipelines/{name}/runs/{num}/nodes/{node}/log/")
    String getBuildNodeLogs(
            @PathVariable("name") String name,
            @PathVariable("num") int num,
            @PathVariable("node") int node
    );

    /**
     * 获取一次构建中的所有节点信息
     * @param name
     * @param num
     * @return
     */
    @GetMapping("blue/rest/organizations/jenkins/pipelines/{name}/runs/{num}/nodes")
    List<Map<String, Object>> getJobBuildNodes(
            @PathVariable("name") String name,
            @PathVariable("num") int num
    );

//    http://jk.choicesaas.cn/blue/rest/organizations/jenkins/pipelines/cicd-test/runs/5/nodes/
//    [{
//        "_class": "io.jenkins.blueocean.rest.impl.pipeline.PipelineNodeImpl",
//                "_links": {
//            "self": {
//                "_class": "io.jenkins.blueocean.rest.hal.Link",
//                        "href": "/blue/rest/organizations/jenkins/pipelines/cicd-test/runs/5/nodes/8/"
//            },
//            "actions": {
//                "_class": "io.jenkins.blueocean.rest.hal.Link",
//                        "href": "/blue/rest/organizations/jenkins/pipelines/cicd-test/runs/5/nodes/8/actions/"
//            },
//            "steps": {
//                "_class": "io.jenkins.blueocean.rest.hal.Link",
//                        "href": "/blue/rest/organizations/jenkins/pipelines/cicd-test/runs/5/nodes/8/steps/"
//            }
//        },
//        "actions": [],
//        "displayDescription": null,
//                "displayName": "Git Parameter",
//                "durationInMillis": 20451,
//                "id": "8",
//                "input": null,
//                "result": "SUCCESS",
//                "startTime": "2020-02-25T14:54:09.184+0800",
//                "state": "FINISHED",
//                "type": "STAGE",
//                "causeOfBlockage": null,
//                "edges": [{
//            "_class": "io.jenkins.blueocean.rest.impl.pipeline.PipelineNodeImpl$EdgeImpl",
//                    "id": "13",
//                    "type": "STAGE"
//        }],
//        "firstParent": null,
//                "restartable": true
//    }]



}
