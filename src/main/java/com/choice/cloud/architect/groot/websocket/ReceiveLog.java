package com.choice.cloud.architect.groot.websocket;

import com.choice.cloud.architect.groot.util.WebSocketUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.Session;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName ReciveLog
 * @Description TODO
 * @Author Guangshan Wang
 * @Date 2020/6/17/017 14:49
 */
public class ReceiveLog implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ReceiveLog.class);

    private Session session;
    private InputStream output;
    private String podId;

    public ReceiveLog(Session session, InputStream output, String podId) {
        this.session = session;
        this.output = output;
        this.podId = podId;
    }

    int len = 0;
    byte[] logChar = new byte[1024];

    @Override
    public void run() {
        try {
            logger.info("开始监听k8s日志 podId:{}", podId);
            while ((len = IOUtils.read(output, logChar)) != -1) {
                if (session == null || !session.isOpen()) {
                    logger.info("连接已关闭 podId:{}", podId);
                    break;
                }
                logger.info("发送日志... podId:{},{}", podId, new String(logChar, 0, len));
                WebSocketUtils.sendMessage(session, new String(logChar, 0, len));
                TimeUnit.SECONDS.sleep(1);
            }
        } catch (Exception e) {
            logger.error("发送实时日志出错 podId:{},error:{}", podId, e);
            e.printStackTrace();
        }
    }
}
