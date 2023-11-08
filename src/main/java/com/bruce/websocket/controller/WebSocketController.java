package com.bruce.websocket.controller;

import com.bruce.websocket.entity.MessageBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Copyright Copyright © 2021 fanzh . All rights reserved.
 * @Desc
 * @ProjectName websocket
 * @Date 2021/2/4 21:53
 * @Author Bruce
 */
@Controller
public class WebSocketController {

    @Autowired
    private SimpMessageSendingOperations simpMessageSendingOperations;

    @RequestMapping("websocket")
    public String websocket() {
        return "websocket";
    }

    @RequestMapping("index")
    public String index() {
        return "index";
    }

    @RequestMapping("qrCode")
    public String qrCode() {
        return "qrCode";
    }

    @RequestMapping("login")
    public String login() {
        return "login";
    }


    /**
     * 广播发送消息，将消息发送到指定的目标地址
     */
    @MessageMapping("/test")
    public void sendTopicMessage(MessageBody messageBody) {
        // 将消息发送到 WebSocket 配置类中配置的代理中（/topic）进行消息转发
        simpMessageSendingOperations.convertAndSend(messageBody.getDestination(), messageBody);
    }


}
