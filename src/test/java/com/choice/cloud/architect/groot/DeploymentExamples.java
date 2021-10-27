package com.choice.cloud.architect.groot;

import com.alibaba.fastjson.JSON;
import com.choice.cloud.architect.groot.constants.K8sConstants;
import io.fabric8.knative.client.DefaultKnativeClient;
import io.fabric8.knative.client.KnativeClient;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.client.*;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.dsl.*;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhangkun
 */
public class DeploymentExamples {
    private static final Logger logger = LoggerFactory.getLogger(DeploymentExamples.class);

    public static void main(String[] args) throws InterruptedException, IOException {
        //System.setProperty("kubeconfig", "D:\\IdeaProjects2\\groot\\src\\test\\resources\\kubeConfig.yml");
        InputStream inputStream = DeploymentExamples.class.getClassLoader().getResourceAsStream("kubeConfig.yml");
        String kubeconfigContents = IOUtils.toString(inputStream);
        Config config = Config.fromKubeconfig(kubeconfigContents);
        config.setTrustCerts(false);

        //InputStream inputStream = DeploymentExamples.class.getClassLoader().getResourceAsStream("kubeConfig.yml");
        //DefaultKubernetesClient defaultKubernetesClient = DefaultKubernetesClient.fromConfig(inputStream);
        KubernetesClient client = new DefaultKubernetesClient(config);

        KnativeClient knativeClient = new DefaultKnativeClient(config);
        try {
            /*NonNamespaceOperation<Namespace, NamespaceList, DoneableNamespace, Resource<Namespace, DoneableNamespace>> namespaces = client.namespaces();
            NamespaceList namespaceList = namespaces.list();
            System.out.println(JSON.toJSONString(namespaceList));

            LabelSelector labelSelector = new LabelSelector();
            Map<String, String> matchLabels = Maps.newHashMap();
            matchLabels.put("workload.user.cattle.io/workloadselector", "deployment-dev-stable-user-gate-gray");
            labelSelector.setMatchLabels(matchLabels);
            FilterWatchListDeletable<Deployment, DeploymentList, Boolean, Watch, Watcher<Deployment>> de = client.apps().deployments().inNamespace("dev-stable")
                    .withLabelIn("workload.user.cattle.io/workloadselector", "deployment-dev-stable-user-gate-gray", "x");
            DeploymentList deploymentList = de.list();

            System.out.println(JSON.toJSONString(deploymentList));*/


            /*FilterWatchListDeletable<Pod, PodList, Boolean, Watch, Watcher<Pod>> pods = client.pods().inNamespace("test-stable")
                    .withLabelIn("workload.user.cattle.io/workloadselector", "deployment-test-stable-basicinfo");
            PodList podList = pods.list();
            System.out.println(JSON.toJSONString(podList));*/

            /*RollbackConfig rollbackConfig = new RollbackConfig();

            rollbackConfig.setRevision(0L);

            DeploymentRollback deploymentRollback = new DeploymentRollback();
            deploymentRollback.setRollbackTo(rollbackConfig);
            deploymentRollback.setName("groot");

            Status rollback = client.apps().deployments().inNamespace("dev-stable").withName("groot").rollback(deploymentRollback);
            System.out.println(rollback);*/

            /*DeploymentRollback deploymentRollback = new DeploymentRollbackBuilder()
                    .withName("user-gate-gray")
                    .withNewRollbackTo().withRevision(5L).endRollbackTo()
                    .build(); */

            /*Status status = new StatusBuilder().build();
            KubernetesClient client = server.getClient();
            server.expect()
                    .post()
                    .withPath("/apis/extensions/v1beta1/namespaces/test/deployments/deployment1/rollback")
                    .andReturn(201, status).once();*/

            //client.extensions().deployments().inNamespace("dev-stable").withName("user-gate-gray").rollback(deploymentRollback);

            //RollableScalableResource<Deployment, DoneableDeployment> replicaSets = client.apps().deployments().inNamespace("dev-stable").withName("user-gate-gray");
            //replicaSets.rolling();

            //NonNamespaceOperation<Revision, RevisionList, DoneableRevision, Resource<Revision, DoneableRevision>> revisions = knativeClient.revisions().inNamespace("dev-stable");

            //System.out.println(JSON.toJSONString(revisions.list()));

//            LogWatch logWatch = client.pods().inNamespace("test-stable").withName("basicinfo-749cb45b79-nsmtx").inContainer("basicinfo")
//                    .watchLog();
//            InputStream output = logWatch.getOutput();
//
//            int len = 0;
//            byte[] logChar = new byte[1024];
//            while ((len = IOUtils.read(output, logChar)) != -1) {
//                System.out.println(new String(logChar, 0, len));
//            }

            FilterWatchListDeletable<Node, NodeList, Boolean, Watch, Watcher<Node>> nodes
                    = client.nodes().withLabelIn("nodeldc","ldc01");
            List<Node> nodeList = nodes.list().getItems();
            nodeList.forEach(item->{
                System.out.println("nodeip:"+item.getMetadata().getLabels().get("kubernetes.io/hostname"));
                System.out.println("allocatable:--------------------");
                System.out.println("cpu:"+item.getStatus().getAllocatable().get("cpu").getAmount());
                System.out.println("memory:"+item.getStatus().getAllocatable().get("memory").getAmount());
                System.out.println("pods:"+item.getStatus().getAllocatable().get("pods").getAmount());
                System.out.println("capacity:--------------------");
                System.out.println("cpu:"+item.getStatus().getCapacity().get("cpu").getAmount());
                System.out.println("memory:"+item.getStatus().getCapacity().get("memory").getAmount());
                System.out.println("pods:"+item.getStatus().getCapacity().get("pods").getAmount());

            });
            System.out.println(JSON.toJSONString(nodes.list().getItems()));
//
//            FilterWatchListDeletable<Node, NodeList, Boolean, Watch, Watcher<Node>> nodeNodeListBooleanWatchWatcherFilterWatchListDeletable = client.nodes().withLabelIn("pod_ldc", "ldc01");
//
//            NodeList list = nodeNodeListBooleanWatchWatcherFilterWatchListDeletable.list();
//
//            System.out.println(JSON.toJSONString(list));
//            MixedOperation<Pod, PodList, DoneablePod, PodResource<Pod, DoneablePod>> pods
//                    = client.pods().list().;
            //System.out.println(JSON.toJSONString(client.pods().withLabel("kubernetes.io/hostname","172.17.31.95").list()));

//            StatefulSet statefulSet = client.apps().statefulSets().inNamespace("test-stable").withName("sms-platform-idc01-ldc01-stable-0001").get();
//            StatefulSet newStatefulSet = new StatefulSet();
//            BeanUtils.copyProperties(statefulSet, newStatefulSet);
//            ObjectMeta metadata = statefulSet.getMetadata();
//            metadata.setName("sms-platform-idc01-ldc01-stable-0004");
//            metadata.setResourceVersion(null);
//            newStatefulSet.setMetadata(metadata);
//            client.apps().statefulSets().create(newStatefulSet);

//            FilterWatchListDeletable<Pod, PodList, Boolean, Watch, Watcher<Pod>> podList = client.pods().inNamespace("test-stable")
//                    .withLabelIn(K8sConstants.K8S_LABEL_POD_APPNAME, "gray-config-center")
//                    .withLabelIn("pod_idc", "idc01")
//                    .withLabelIn("pod_ldc", "ldc01");
//
//            System.out.println(JSON.toJSONString(podList.list()));

            NamespaceList list = client.namespaces().list();
            System.out.println(JSON.toJSONString(list));
        } finally {
            client.close();
        }
    }
}
