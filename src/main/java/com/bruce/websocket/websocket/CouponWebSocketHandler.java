package com.bruce.websocket.websocket;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Copyright Copyright © 2021 fanzh . All rights reserved.
 * @Desc
 * @ProjectName websocket
 * @Date 2021/12/16 10:44
 * @Author fzh
 */
@Component
@Slf4j
public class CouponWebSocketHandler extends TextWebSocketHandler {

    public final static Map<String, WebSocketSession> CONNECTIONS = new ConcurrentHashMap<>(8);

    /**
     * 给客户端主动发消息 单一发送
     */
    public void sendMessage(String couponId, String message) throws IOException {
        WebSocketSession session = CONNECTIONS.get(couponId);
        if (session != null) {
            session.sendMessage(new TextMessage(message));
        }
    }

    /**
     * 定时清理过期连接
     */
    @Scheduled(cron = "1 1,30 * * * ?")
    private void timerClean() {
        log.info("websocket clean start");
        Set<Map.Entry<String, WebSocketSession>> set = CONNECTIONS.entrySet();
        for (Map.Entry<String, WebSocketSession> sessionMap : set) {
            if (!sessionMap.getValue().isOpen()) {
                try {
                    sessionMap.getValue().close();
                } catch (IOException e) {
                    log.error("websocket close error:{}", e.getMessage(), e);
                }
                CONNECTIONS.remove(sessionMap.getKey());

            }
        }
    }

    private static String getCouponId(WebSocketSession session) {
        if (session.getUri() == null) {
            return null;
        }
        MultiValueMap<String, String> params = UriComponentsBuilder.fromUri(session.getUri()).build().getQueryParams();
        List<String> parameters = params.get("couponId");
        if (CollectionUtils.isEmpty(parameters)) {
            return null;
        }
        String parameter = parameters.get(0);
        if (StringUtils.isBlank(parameter)) {
            return null;
        }
        return StringUtils.trim(parameter);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        log.info("websocket#handleTextMessage:{},{}", message.getPayload(), session.getRemoteAddress());
        String couponId = getCouponId(session);
        if (couponId == null) {
            close(session);
        }
        // 获取 id，查询是否已核销
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("websocket#afterConnectionEstablished:{},{}", getCouponId(session), session.getRemoteAddress());
        String couponId = getCouponId(session);
        if (couponId == null) {
            close(session);
        }
        //将连接信息放到CONNECTIONS中
        CONNECTIONS.put(couponId, session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("websocket#afterConnectionClosed:{},{}", getCouponId(session), session.getRemoteAddress());
        close(session);
    }

    private void close(WebSocketSession session) throws IOException {
        String couponId = getCouponId(session);
        if (couponId != null) {
            //将连接信息从CONNECTIONS中删除
            CONNECTIONS.remove(couponId);
        }
        session.close();
    }

}
