package com.bruce.websocket.websocket;

import com.bruce.websocket.util.UrlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Copyright Copyright © 2021 fanzh . All rights reserved.
 * @Desc
 * @ProjectName websocket
 * @Date 2021/2/4 21:53
 * @Author Bruce
 */
@ServerEndpoint(value = "/my")
@Component
@Slf4j
public class MyWebSocket {

    /**
     * concurrent包的线程安全Map，用来存放每个客户端对应的Session对象。
     */
    private final static Map<String, Session> CONNECTIONS = new ConcurrentHashMap<>();

    /**
     * 连接建立成功调用的方法
     *
     * @param session Session
     */
    @OnOpen
    public void onOpen(Session session) throws IOException {
        log.info("连接建立");
        String id = getId(session);
        if (StringUtils.isBlank(id)) {
            close(session);
            return;
        }
        CONNECTIONS.put(id, session);
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) throws IOException {
        log.info("连接关闭");
        close(session);
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message message
     * @param session session
     * @throws IOException
     */
    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        String id = getId(session);
        if (StringUtils.isBlank(id)) {
            close(session);
            return;
        }
        sendMessage(id, message);
    }


    /**
     * 发生错误时调用
     *
     * @param session Session
     * @param error   Throwable
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.info("发生错误");
        error.printStackTrace();
    }

    private void close(Session session) throws IOException {
        String id = getId(session);
        if (StringUtils.isNotBlank(id)) {
            CONNECTIONS.remove(id);
        }
        session.close();
    }

    private String getId(Session session) {
        String queryString = session.getQueryString();
        Map<String, String> map = UrlUtil.getParameter(queryString);
        return map.get("id");
    }

    /**
     * 发送信息
     */
    public void sendMessage(String id, String message) throws IOException {
        Session session = CONNECTIONS.get(id);
        if (session != null) {
            //获取session远程基本连接发送文本消息
            session.getBasicRemote().sendText(message);
        }
    }

    /**
     * 定时清理过期连接
     */
    @Scheduled(cron = "1 1,30 * * * ?")
    private void timerClean() throws IOException {
        log.info("websocket clean start");
        Set<String> keySet = CONNECTIONS.keySet();
        for (String key : keySet) {
            close(CONNECTIONS.get(key));
        }
    }

}
