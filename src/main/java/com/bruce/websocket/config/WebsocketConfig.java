package com.bruce.websocket.config;

import com.bruce.websocket.websocket.CouponWebSocketHandler;
import com.bruce.websocket.websocket.WebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * @Copyright Copyright © 2021 fanzh . All rights reserved.
 * @Desc
 * @ProjectName websocket
 * @Date 2021/2/4 21:53
 * @Author Bruce
 */
@Configuration
@EnableWebSocket
public class WebsocketConfig implements WebSocketConfigurer {

    @Autowired
    private WebSocketHandler webSocketHandler;

    @Autowired
    private CouponWebSocketHandler couponWebSocketHandler;


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler, "/v2/my").setAllowedOrigins("*");
        registry.addHandler(couponWebSocketHandler, "/v2/coupon").setAllowedOrigins("*");
    }
}
