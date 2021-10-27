//package com.choice.cloud.architect.groot;
//
//import io.kubernetes.client.ApiClient;
//import io.kubernetes.client.ApiException;
//import io.kubernetes.client.Configuration;
//import io.kubernetes.client.apis.CoreV1Api;
//import io.kubernetes.client.models.V1Pod;
//import io.kubernetes.client.models.V1PodList;
//import io.kubernetes.client.util.ClientBuilder;
//import io.kubernetes.client.util.KubeConfig;
//
//import java.io.FileReader;
//import java.io.IOException;
//
///**
// * @author zhangkun
// */
//public class KubeConfigFileClientExample {
//    public static void main(String[] args) throws IOException, ApiException {
//
//        // file path to your KubeConfig
//        String kubeConfigPath = "D:\\IdeaProjects2\\groot\\src\\test\\resouces\\kubeConfig.yml";
//
//        // loading the out-of-cluster config, a kubeconfig from file-system
//        ApiClient client =
//                ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(new FileReader(kubeConfigPath))).build();
//
//        // set the global default api-client to the in-cluster one from above
//        Configuration.setDefaultApiClient(client);
//
//        // the CoreV1Api loads default api-client from global configuration.
//        CoreV1Api api = new CoreV1Api();
//
//        // invokes the CoreV1Api client
//        V1PodList list =
//                api.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null);
//        for (V1Pod item : list.getItems()) {
//            System.out.println(item.getMetadata().getName());
//        }
//    }
//}
