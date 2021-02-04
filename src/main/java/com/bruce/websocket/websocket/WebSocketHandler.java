package com.bruce.websocket.websocket;

import com.alibaba.fastjson.JSON;
import com.bruce.websocket.entity.RequestContent;
import com.bruce.websocket.util.UrlUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * @Copyright Copyright © 2021 fanzh . All rights reserved.
 * @Desc
 * @ProjectName websocket
 * @Date 2021/2/4 21:53
 * @Author Bruce
 */
@Component
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {


    /**
     * socket 建立成功事件
     *
     * @param session
     * @throws Exception
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("连接建立");
        RequestContent requestContent = getRequestContent(session);
        log.info("requestContent:{}", requestContent);
        WsSessionManager.add(String.valueOf(requestContent.getId()), session);

    }

    /**
     * 接收消息事件
     *
     * @param session
     * @param message
     * @throws Exception
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 获得客户端传来的消息
        String payload = message.getPayload();
        log.info("message:{}", payload);
        RequestContent requestContent = getRequestContent(session);
        log.info("requestContent:{}", requestContent);
        session.sendMessage(new TextMessage(LocalDateTime.now().toString()));
    }

    /**
     * socket 断开连接时
     *
     * @param session
     * @param status
     * @throws Exception
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("连接断开");
        RequestContent requestContent = getRequestContent(session);
        log.info("requestContent:{}", requestContent);
        // 用户退出，移除缓存
        WsSessionManager.remove(String.valueOf(requestContent.getId()));
    }


    private RequestContent getRequestContent(WebSocketSession session) {
        URI uri = session.getUri();
        if (uri == null) {
            return null;
        }
        Map<String, String> map = UrlUtil.getParameter(uri.getQuery());
        return JSON.parseObject(JSON.toJSONString(map), RequestContent.class);
    }

}
