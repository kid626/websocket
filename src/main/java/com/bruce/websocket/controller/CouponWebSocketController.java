package com.bruce.websocket.controller;

import com.bruce.websocket.websocket.CouponWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Copyright Copyright © 2021 fanzh . All rights reserved.
 * @Desc
 * @ProjectName websocket
 * @Date 2021/12/16 11:04
 * @Author fzh
 */
@RestController
public class CouponWebSocketController {

    @Autowired
    private CouponWebSocketHandler handler;

    @GetMapping("/send")
    public void send(String msg) throws Exception {
        handler.sendMessage("1", msg);
    }

}
