package com.choice.cloud.architect.groot.util;

import lombok.extern.slf4j.Slf4j;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.io.IOException;

/**
 * @author zhangkun
 */
@Slf4j
public class WebSocketUtils {
    /**
     * @param session 容器 session
     * @param message 发送内容
     */
    public static void sendMessage(Session session, String message) {
        if (session == null || !session.isOpen()) {
            return;
        }

        final RemoteEndpoint.Basic basic = session.getBasicRemote();

        if (basic == null) {
            return;
        }

        try {
            basic.sendText(message);
        } catch (IOException e) {
            log.error("sendMessage IOException ", e);
        }
    }
}
