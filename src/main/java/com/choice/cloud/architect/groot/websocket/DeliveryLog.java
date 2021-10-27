package com.choice.cloud.architect.groot.websocket;

import com.choice.cloud.architect.groot.request.k8s.PodLogRequest;
import com.choice.cloud.architect.groot.util.WebSocketUtils;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.websocket.Session;
import java.util.concurrent.TimeUnit;

/**
 * @Author: zhangguoquan
 * @Date: 2020/7/15 10:05
 */
@Slf4j
@AllArgsConstructor
public class DeliveryLog implements Runnable{

    private Session session;
    private PodLogRequest request;
    private KubernetesClient client;

    @Override
    public void run() {
        String podName = request.getPodName();
        try {
            log.info("开始监听k8s日志 podName:{}", podName);
            final int maxRequestCount = 100;
            int requestCount = 0;
            while (requestCount < maxRequestCount) {
                if (session == null || !session.isOpen()) {
                    log.info("连接已关闭 podName:{}", podName);
                    break;
                }
                log.info("发送日志... podName:{}", podName);

                String namespaceName = request.getNamespace();
                String sourceLog = "";
                if (requestCount == 0) {
                    sourceLog = client
                            .pods()
                            .inNamespace(namespaceName)
                            .withName(podName)
                            .tailingLines(50)
                            .withPrettyOutput()
                            .getLog();
                } else {
                    sourceLog = client
                            .pods()
                            .inNamespace(namespaceName)
                            .withName(podName)
//                        .terminated()
                            .sinceSeconds(request.getPeriodSeconds())
                            .tailingLines(request.getTailLines())
                            .withPrettyOutput()
                            .getLog();
                }

                StringBuilder logBuilder = new StringBuilder();
//                Integer lineLength = request.getLineLength();

                if (StringUtils.isNotBlank(sourceLog)) {
                    // 后端换行
                    logBuilder.append(sourceLog.replace("\n", "<br>"));
//                    boolean hasLn = false;
//                    int i = 0;
//                    while (i < sourceLog.length()) {
//                        int endIndex = i + lineLength;
//                        String s = sourceLog.substring(i, Math.min(endIndex, sourceLog.length()));
//                        hasLn = s.contains("\n");
//                        if (hasLn) {
//                            int indexLn = s.indexOf("\n");
//                            s = sourceLog.substring(i, i + indexLn + 1);
//                            logBuilder.append(s).append("</br></br>");
//                            i = i + indexLn + 1;
//                        } else {
//                            logBuilder.append(s);
//                            i += lineLength;
//                        }
//                    }
                }
                WebSocketUtils.sendMessage(session, logBuilder.toString());
                TimeUnit.SECONDS.sleep(request.getPeriodSeconds());
                requestCount++;
            }
        } catch (Exception e) {
            log.error("发送实时日志出错 podName:{},error:{}", podName, e);
        }

    }
}
