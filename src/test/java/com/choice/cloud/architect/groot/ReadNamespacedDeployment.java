package com.choice.cloud.architect.groot;

import com.choice.cloud.architect.groot.util.WebSocketUtils;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.apps.ReplicaSet;
import io.fabric8.kubernetes.api.model.apps.ReplicaSetList;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.ExecListener;
import io.fabric8.kubernetes.client.dsl.ExecWatch;
import io.fabric8.kubernetes.client.dsl.LogWatch;
import io.fabric8.openshift.client.OpenShiftClient;
import okhttp3.Response;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.rules.Timeout;

import java.io.*;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangkun
 */
public class ReadNamespacedDeployment {
    public static void main(String[] args) throws IOException {
//        Boolean export = true; // Boolean | Should this value be exported.  Export strips fields that a user can not specify. Deprecated. Planned for removal in 1.18.
        try {
//            V1beta2Deployment result = apiInstance.readNamespacedDeployment(name, namespace, pretty, exact, export);
//            System.out.println(result);
            //System.out.println(result.getSpec().getReplicas());

            InputStream inputStream = DeploymentExamples.class.getClassLoader().getResourceAsStream("kubeConfig.yml");
            String kubeconfigContents = IOUtils.toString(inputStream);
            Config config = Config.fromKubeconfig(kubeconfigContents);
            config.setTrustCerts(false);

            //InputStream inputStream = DeploymentExamples.class.getClassLoader().getResourceAsStream("kubeConfig.yml");
            //DefaultKubernetesClient defaultKubernetesClient = DefaultKubernetesClient.fromConfig(inputStream);
            KubernetesClient client = new DefaultKubernetesClient(config);

            ReplicaSetList replicaSetList = client.apps().replicaSets().inNamespace("test-stable")
                    .withLabel("workload.user.cattle.io/workloadselector","deployment-test-stable-gray-config-center").list();
            System.out.println("1");
//            LogWatch logWatch = client.pods().inNamespace("pro-pre").withName("choice-smp-bf7dd7d5c-zddr6")
//                    .inContainer("choice-smp").tailingLines(10000).watchLog(System.out);
//
//            System.out.println(logWatch);
//
//            InputStream output = logWatch.getOutput();
//
//            int len = 0;
//            byte[] logChar = new byte[1024];
//
//            while ((len = IOUtils.read(output, logChar)) != -1) {
//
//                System.out.println("发送日志... {}" + new String(logChar, 0, len));
//            }

//            ScaleSpec scaleSpec = new ScaleSpec();
//            scaleSpec.setReplicas(2);
//            Scale scaleObj = new ScaleBuilder()
//                    .withSpec(scaleSpec)
//                    .build();
//           client.apps().deployments().inNamespace("c7p-dev").withName("k8stestzgq").scale(2, true);
//            Scale scaleResponse  = client.apps().deployments().inNamespace("c7p-dev").withName("k8stestzgq").scale();
//            System.out.println(scaleResponse);
//            LabelSelector labelSelector = new LabelSelector();
            Map<String, String> map = client.apps().deployments().inNamespace("test-stable")
                .withName("gray-config-center").get().getMetadata().getAnnotations();
            System.out.println(map.get("deployment.kubernetes.io/revision"));
//                    .withNewMetadata()
//                    .addToAnnotations("fabric8.io/iconUrl", "img/icons/spring-boot.svg")
//                    .addToAnnotations("fabric8.io/metrics-path", "dashboard/file/kubernetes-pods.json/?var-project=fabric8-maven-sample-zero-config&var-version=3.5-SNAPSHOT")
//                    .addToAnnotations("fabric8.io/scm-url",  "https://github.com/spring-projects/spring-boot/spring-boot-starter-parent/fabric8-maven-sample-zero-config")
//                    .addToLabels("app", "fabric8-maven-sample-zero-config")
//                    .addToLabels("provider", "fabric8")
//                    .addToLabels("version", "3.5-SNAPSHOT")
//                    .addToLabels("group", "io.fabric8")
//                    .withName("fabric8-maven-sample-zero-config")
//                    .withClusterName("kubernetes")
//                    .withCreationTimestamp("2017-11-01 13:21:22 UTC")
//                    .withDeletionTimestamp("2017-11-02 13:21:22 UTC")
//                    .withNamespace("myproject")
//                    .withGenerateName("zero-config-test")
//                    .withOwnerReferences()
//                    .endMetadata()
//                    .withNewSpec()
//                    .withMinReadySeconds(5)
//                    .withPaused(false)
//                    .withReplicas(5)
//                    .withRevisionHistoryLimit(3)
//                    .withNewSelector()
//                    .addToMatchLabels("app", "fabric8-maven-sample-zero-config")
//                    .addToMatchLabels("provider", "fabric8")
//                    .addToMatchLabels("group", "io.fabric8")
//                    .endSelector()
//                    .withNewStrategy()
//                    .withType("Rolling")
//                    .withNewRollingUpdate()
//                    .withMaxSurge(new IntOrString(20))
//                    .withMaxUnavailable(new IntOrString(20))
//                    .endRollingUpdate()
//                    .endStrategy()
//                    .withNewTemplate()
//                    .withNewMetadata()
//                    .addToAnnotations("fabric8.io/metrics-path", "dashboard/file/kubernetes-pods.json/?var-project=fabric8-maven-sample-zero-config&var-version=3.5-SNAPSHOT")
//                    .addToAnnotations("fabric8.io/scm-url", "https://github.com/spring-projects/spring-boot/spring-boot-starter-parent/fabric8-maven-sample-zero-config")
//                    .addToAnnotations("fabric8.io/iconUrl", "img/icons/spring-boot.svg")
//                    .addToLabels("app", "fabric8-maven-sample-zero-config")
//                    .addToLabels("provider", "fabric8")
//                    .addToLabels("version", "3.5-SNAPSHOT")
//                    .addToLabels("group", "io.fabric8")
//                    .endMetadata()
//                    .withNewSpec()
//                    .withActiveDeadlineSeconds(new Long(10))
//                    .addNewContainer()
//                    .addToCommand("printenv")
//                    .addToArgs("HOSTNAME", "KUBERNETES_PORT")
//                    .addNewEnv()
//                    .withName("KUBERNETES_NAMESPACE")
//                    .withNewValueFrom()
//                    .withNewFieldRef()
//                    .endFieldRef()
//                    .endValueFrom()
//                    .endEnv()
//                    .endContainer() .endSpec() .endTemplate() .endSpec() .done();
//            client.apps().deployments().inNamespace("test-20200501-choice-join").withName("choicecloud-manage").delete();
//            client.autoscaling().horizontalPodAutoscalers().inNamespace("c7p-dev")
//                    .withName("k8stestzgq").create(horizontalPodAutoscaler);
//            System.out.println(horizontalPodAutoscaler);

//            client.replicationControllers().inNamespace("test-stable")
//                    .withName("gray-config-center").rolling()
//                    .withTimeout(6, TimeUnit.SECONDS).updateImage("r.cn/test/test-stable/gray-config-center:36");
//            String l1 = client.pods().inNamespace("test-stable").withName("gray-config-center-5845b9b69b-74s6h").withPrettyOutput().getLog();
//
//            String l2 = client.pods().inNamespace("test-stable").withName("gray-config-center-5845b9b69b-74s6h").getLog();
//
//            String l7 = client.pods().inNamespace("test-stable").withName("gray-config-center-5845b9b69b-74s6h").inContainer("gray-config-center").withPrettyOutput().getLog();
//
//            String l8 = client.pods().inNamespace("test-stable").withName("gray-config-center-5845b9b69b-74s6h").limitBytes(100).getLog();
//            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(input.getBytes());



//            InputStreamReader reader = new InputStreamReader(System.in);
//            String input = new BufferedReader(reader).readLine();

//            java.io.PrintStream@12bcd0c0
//            java.io.BufferedInputStream@4879f0f2



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class SimpleListener implements ExecListener {

        @Override
        public void onOpen(Response response) {
            System.out.println("The shell will remain open for 10 seconds.");
        }

        @Override
        public void onFailure(Throwable t, Response response) {
            System.err.println("shell barfed");
        }

        @Override
        public void onClose(int code, String reason) {
            System.out.println("The shell will now close.");
        }
    }
}
