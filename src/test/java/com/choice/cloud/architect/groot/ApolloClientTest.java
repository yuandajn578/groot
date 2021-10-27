package com.choice.cloud.architect.groot;

import com.alibaba.fastjson.JSONArray;
import com.choice.cloud.architect.groot.service.inner.ApolloClientService;
import com.ctrip.framework.apollo.openapi.dto.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApolloClientTest {

    @Autowired
    private ApolloClientService apolloClientService;


    @Test
    public void getAllEnvClusters() {
        List<OpenEnvClusterDTO> allEnvClusters = apolloClientService.getAllEnvClusters("basicinfo");
        String s = JSONArray.toJSONString(allEnvClusters);
        System.err.println(s);
    }

    @Test
    public void getAllClusterNamespaces() {
        List<OpenNamespaceDTO> allClusterNamespaces = apolloClientService.getAllClusterNamespaces("coss", "dev", "default");
        String s = JSONArray.toJSONString(allClusterNamespaces);
        System.err.println(s);
    }

    @Test
    public void getClusterAllNamespaces() {
        OpenNamespaceDTO clusterAllNamespaces = apolloClientService.getClusterAllNamespaces("coss", "dev", "default", "application");
        String s = JSONArray.toJSONString(clusterAllNamespaces);
        System.err.println(s);
    }

    @Test
    public void createAppNamespace() {
        OpenAppNamespaceDTO appNamespaceDTO = new OpenAppNamespaceDTO();
        appNamespaceDTO.setAppId("groot");
        appNamespaceDTO.setName("ext");
        appNamespaceDTO.setComment("ces namep");
        appNamespaceDTO.setPublic(true);
        appNamespaceDTO.setDataChangeCreatedBy("apollo");
        appNamespaceDTO.setDataChangeCreatedTime(new Date());
        OpenAppNamespaceDTO appNamespace = apolloClientService.createAppNamespace(appNamespaceDTO);
        String s = JSONArray.toJSONString(appNamespace);
        System.err.println(s);
    }

    @Test
    public void getNamespaceLock() {
        OpenNamespaceLockDTO namespaceLock = apolloClientService.getNamespaceLock("coss", "dev", "default", "application");
        String s = JSONArray.toJSONString(namespaceLock);
        System.err.println(s);
    }

    @Test
    public void createItem() {
        OpenItemDTO openItemDTO = new OpenItemDTO();
        openItemDTO.setKey("aaa.bbb.ccc");
        openItemDTO.setValue("bbb");
        openItemDTO.setComment("ces");
        openItemDTO.setDataChangeCreatedBy("apollo");
        OpenItemDTO item = apolloClientService.createItem("groot", "dev", "default", "MobileApplications.groot ", openItemDTO);
        String s = JSONArray.toJSONString(item);
        System.err.println(s);
    }

    @Test
    public void updateItem() {
        OpenItemDTO openItemDTO = new OpenItemDTO();
        openItemDTO.setKey("aa.bb.cc");
        openItemDTO.setValue("bbbC");
        openItemDTO.setComment("ces");
        openItemDTO.setDataChangeLastModifiedBy("apollo");
        apolloClientService.updateItem("GROOT","pre","default","application",openItemDTO);
    }

    //TODO
    @Test
    public void createOrUpdateItem() {
        OpenItemDTO openItemDTO = new OpenItemDTO();
        openItemDTO.setKey("aaa.bbb.cccc");
        openItemDTO.setValue("bbb");
        openItemDTO.setComment("ces");
        openItemDTO.setDataChangeLastModifiedBy("apollo");
        openItemDTO.setDataChangeCreatedBy("apollo");
        apolloClientService.createOrUpdateItem("groot","dev","default","MobileApplications.ext",openItemDTO);
    }

    @Test
    public void removeItem() {
        apolloClientService.removeItem("groot","dev","default","MobileApplications.groot","aaa.bbb.ccc","apollo");
    }

    @Test
    public void publishNamespace() {
        NamespaceReleaseDTO releaseDTO = new NamespaceReleaseDTO();
        releaseDTO.setReleasedBy("apollo");
        releaseDTO.setReleaseTitle("发布测试");
        releaseDTO.setEmergencyPublish(true);
        releaseDTO.setReleaseComment("发布测试备注");
        OpenReleaseDTO openReleaseDTO = apolloClientService.publishNamespace("groot", "dev", "default", "MobileApplications.groot", releaseDTO);
        String s = JSONArray.toJSONString(openReleaseDTO);
        System.out.println(s);
    }

    @Test
    public void getLatestActiveRelease() {
        OpenReleaseDTO latestActiveRelease = apolloClientService.getLatestActiveRelease("groot", "dev", "default", "MobileApplications.groot");
        String s = JSONArray.toJSONString(latestActiveRelease);
        System.out.println(s);
    }
}
