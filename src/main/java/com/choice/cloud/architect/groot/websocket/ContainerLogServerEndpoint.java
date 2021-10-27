package com.choice.cloud.architect.groot.websocket;

import com.alibaba.fastjson.JSON;
import com.choice.cloud.architect.groot.enums.EnvTypeEnum;
import com.choice.cloud.architect.groot.request.k8s.PodLogRequest;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

/**
 * @author zhangkun
 */
@Service
@ServerEndpoint("/container-log/{envType}/{envCode}/{podId}/{appCode}")
public class ContainerLogServerEndpoint{
    private static final Logger logger = LoggerFactory.getLogger(ContainerLogServerEndpoint.class);
//    private static final Map<String, InputStream> ONLINE_CONTAINER_LOG_INPUT_STREAM =
//            new ConcurrentHashMap<>();

    private static ApplicationContext applicationContext;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        ContainerLogServerEndpoint.applicationContext = applicationContext;
    }

    private KubernetesClient offlineKubernetesClient;
    private KubernetesClient onlineKubernetesClient;

    @OnOpen
    public void openSession(@PathParam("envType") String envType, @PathParam("envCode") String envCode,
                            @PathParam("podId") String podId, @PathParam("appCode") String appCode, Session session) {
        try {
            offlineKubernetesClient = applicationContext.getBean("offlineKubernetesClient", KubernetesClient.class);
            onlineKubernetesClient = applicationContext.getBean("onlineKubernetesClient", KubernetesClient.class);

            logger.info("openSession envType = {}, envCode = {}, podId = {}, appCode = {}", envType, envCode, podId, appCode);
//            String nsName = getNsName(envType, envCode);
            KubernetesClient client = null;
//            LogWatch logWatch = null;
            final int tailLine = 500;
            if (EnvTypeEnum.test.value().equals(envType)) {
                logger.info("offlineKubernetesClient = {}", JSON.toJSONString(offlineKubernetesClient));
//                logWatch = offlineKubernetesClient.pods().inNamespace(nsName).withName(podId)
//                        .inContainer(appCode).tailingLines(tailLine).watchLog();
                client = offlineKubernetesClient;
            } else if (EnvTypeEnum.pro.value().equals(envType)) {
                logger.info("onlineKubernetesClient = {}", JSON.toJSONString(onlineKubernetesClient));
//                logWatch = onlineKubernetesClient.pods().inNamespace(nsName).withName(podId)
//                        .inContainer(appCode).tailingLines(tailLine).watchLog();
                client = onlineKubernetesClient;
            }

//            if (null == logWatch) {
//                logger.error("查询container 日志失败, null == logWatch");
//                WebSocketUtils.sendMessage(session, "查询应用日志失败");
//            }

//            InputStream output = logWatch.getOutput();
//            if (null == output) {
//                logger.error("查询container 日志失败, null == output");
//            }
//            ONLINE_CONTAINER_LOG_INPUT_STREAM.put(session.getId(), output);
            PodLogRequest request = new PodLogRequest();
            request.setEnvCode(envCode);
            request.setEnvType(envType);
            request.setPodName(podId);
            request.setPeriodSeconds(5);
            request.setTailLines(500);
            request.setLineLength(240);
            Thread deliveryLogThread = new Thread(new DeliveryLog(session, request, client));
            deliveryLogThread.start();

        } catch (Exception e) {
            logger.error("openSession error {}", ExceptionUtils.getStackTrace(e));
        }
    }

    private String getNsName(String envType, String envCode) {
        return envType.concat("-").concat(envCode);
    }

    @OnClose
    public void onClose(@PathParam("envType") String envType, @PathParam("envCode") String envCode,
                        @PathParam("podId") String podId, @PathParam("appCode") String appCode,
                        Session session) {
        try {
            logger.info("监听关闭webSocket session:{},envType:{},envCode:{},podId:{},appCode:{}", session.getId(), envType, envCode, podId, appCode);
//            InputStream inputStream = ONLINE_CONTAINER_LOG_INPUT_STREAM.get(session.getId());
//            ONLINE_CONTAINER_LOG_INPUT_STREAM.remove(session.getId());
            logger.info("监听关闭websocket, envType = {},  podId = {}, appCode = {}", envType, podId, appCode);

//            if (null != inputStream) {
//                logger.info("关闭k8s输入流");
//                IOUtils.closeQuietly(inputStream);
//            }
            session.close();
        } catch (Exception e) {
            logger.error("监听webSocket关闭，处理业务逻辑失败 error:{}", e);
            e.printStackTrace();
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        try {
            logger.error("监听到webSocket错误信息 sessionId:{}", session.getId());
//            InputStream inputStream = ONLINE_CONTAINER_LOG_INPUT_STREAM.get(session.getId());
//            ONLINE_CONTAINER_LOG_INPUT_STREAM.remove(session.getId());
//            if (null != inputStream) {
//                logger.info("关闭k8s输入流");
//                IOUtils.closeQuietly(inputStream);
//            }
            session.close();
        } catch (IOException e) {
            logger.error("onError exception", e);
        }
        logger.error("Throwable msg " + throwable.getMessage());
    }
}
