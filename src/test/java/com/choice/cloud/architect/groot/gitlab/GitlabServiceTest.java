package com.choice.cloud.architect.groot.gitlab;

import io.swagger.annotations.Authorization;
import org.gitlab.api.models.GitlabProject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GitlabServiceTest {

    @Autowired
    GitlabService gitlabService;

    @Test
    public void merge() throws Exception {
        String projectUri = "git@gitlab.choicesoft.com.cn:choice-scm/choice-scm.git";
        String fromBranch = "mq-gray-plugin-2";
        String toBranch = "mq-gray-plugin";
        gitlabService.merge(projectUri, fromBranch, toBranch, "测试合并分支");
    }
}
