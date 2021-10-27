package com.choice.cloud.architect.groot.websocket;

import com.choice.cloud.architect.groot.enums.EnvTypeEnum;
import com.choice.cloud.architect.groot.util.WebSocketUtils;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.ExecListener;
import io.fabric8.kubernetes.client.dsl.ExecWatch;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @Author: zhangguoquan
 * @Date: 2020/7/16 11:09
 */
@Service
@ServerEndpoint("/exec-shell/{envType}/{envCode}/{podId}/{appCode}")
@Slf4j
public class ExecServerEndpoint {
    private static final Map<String, ExecWatch> SESSION_EXEC_WATCH_MAP = new ConcurrentHashMap<>();
    private static ApplicationContext applicationContext;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        ExecServerEndpoint.applicationContext = applicationContext;
    }

    private KubernetesClient client;

    byte[] bytes = new byte[1024];
    int len = 0;

    @OnOpen
    public void openSession(@PathParam("envType") String envType, @PathParam("envCode") String envCode,
                            @PathParam("podId") String podId, @PathParam("appCode") String appCode, Session session) {

        // TODO: 2020/7/16 权限校验

        client = EnvTypeEnum.test.value().equals(envType)
                ? applicationContext.getBean("offlineKubernetesClient", KubernetesClient.class)
                : applicationContext.getBean("onlineKubernetesClient", KubernetesClient.class);

        String nameSpace = getNsName(envType, envCode);
        PipedInputStream output = new PipedInputStream();
        PipedInputStream error = new PipedInputStream();
        ExecWatch watch = client.pods().inNamespace(nameSpace).withName(podId)
                .redirectingInput()
                .readingOutput(output)
                .readingError(error)
                .withTTY()
                .usingListener(new ExecListener() {
                    @Override
                    public void onOpen(Response response) {
                        log.info("shell start");
                    }

                    @Override
                    public void onFailure(Throwable t, Response response) {
                        log.error("shell barfed");
                    }

                    @Override
                    public void onClose(int code, String reason) {
                        log.info("The shell will now close.");
                    }
                })
                .exec("env", "TERM=xterm", "COLUMNS=180", "LINES=50", "bash");
        SESSION_EXEC_WATCH_MAP.put(session.getId(), watch);

        InputStream watchOutput = watch.getOutput();

        try {
            TimeUnit.SECONDS.sleep(1);
            len = watchOutput.read(bytes);
            log.info("console = {}", new String(bytes, 0, len));
            WebSocketUtils.sendMessage(session, new String(bytes, 0, len));
        } catch (Exception e) {
            log.error("output error");
        }

    }

    @OnMessage
    public void onMessage(String message, Session session) {
        ExecWatch watch = SESSION_EXEC_WATCH_MAP.get(session.getId());
        OutputStream watchInput = watch.getInput();
        InputStream watchOutput = watch.getOutput();
        try {
            log.info("write msg = {}", message);
            watchInput.write(message.getBytes());
            TimeUnit.SECONDS.sleep(1);
            len = watchOutput.read(bytes);
            log.info("console = {}", new String(bytes, 0, len));
            WebSocketUtils.sendMessage(session, new String(bytes, 0, len));
        } catch (Exception e) {
            log.error("send message error");
        }
    }

    @OnClose
    public void onClose(@PathParam("envType") String envType, @PathParam("envCode") String envCode,
                        @PathParam("podId") String podId, @PathParam("appCode") String appCode,
                        Session session) {
        ExecWatch watch = SESSION_EXEC_WATCH_MAP.get(session.getId());
        WebSocketUtils.sendMessage(session, "The shell will now close.");
        watch.close();
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        ExecWatch watch = SESSION_EXEC_WATCH_MAP.get(session.getId());
        watch.close();
    }

    private String getNsName(String envType, String envCode) {
        return envType.concat("-").concat(envCode);
    }
}
