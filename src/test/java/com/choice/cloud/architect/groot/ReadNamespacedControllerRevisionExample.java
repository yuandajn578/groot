package com.choice.cloud.architect.groot;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.AppsV1beta1Api;
import io.kubernetes.client.models.V1beta1ControllerRevision;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;

import java.io.FileReader;
import java.io.IOException;

/**
 * @author zhangkun
 */
public class ReadNamespacedControllerRevisionExample {

    public static void main(String[] args) throws IOException {
        // file path to your KubeConfig
        String kubeConfigPath = "D:\\IdeaProjects2\\groot\\src\\test\\resources\\kubeConfig.yml";

        // loading the out-of-cluster config, a kubeconfig from file-system
        ApiClient defaultClient =
                ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(new FileReader(kubeConfigPath))).build();

        // set the global default api-client to the in-cluster one from above
        Configuration.setDefaultApiClient(defaultClient);

        AppsV1beta1Api apiInstance = new AppsV1beta1Api(defaultClient);
        String name = "groot-gray"; // String | name of the ControllerRevision
        String namespace = "dev-stable"; // String | object name and auth scope, such as for teams and projects
        String pretty = "true"; // String | If 'true', then the output is pretty printed.
        Boolean exact = true; // Boolean | Should the export be exact.  Exact export maintains cluster-specific fields like 'Namespace'. Deprecated. Planned for removal in 1.18.
        Boolean export = true; // Boolean | Should this value be exported.  Export strips fields that a user can not specify. Deprecated. Planned for removal in 1.18.
        try {
            V1beta1ControllerRevision result = apiInstance.readNamespacedControllerRevision(name, namespace, pretty, exact, export);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling AppsV1beta1Api#readNamespacedControllerRevision");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
