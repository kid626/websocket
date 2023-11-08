package com.bruce.websocket.controller;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import com.alibaba.fastjson.JSONObject;
import com.bruce.websocket.websocket.QrCodeWebSocketServer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @Copyright Copyright © 2023 fanzh . All rights reserved.
 * @Desc
 * @ProjectName websocket
 * @Date 2023/11/8 17:51
 * @Author Bruce
 */
@RestController
public class QrCodeController {

    private Map<String, String> MAP = new HashMap<>();


    //获取登录二维码、放入Token
    @GetMapping(value = "/getQrCode")
    public void createCodeImg(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");

        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");

        try {
            //这里没啥操作 就是生成一个UUID插入 数据库的表里
            String uuid = RandomUtil.randomString(32);
            MAP.put(uuid, uuid);
            response.setHeader("uuid", uuid);
            // 这里是开源工具类 hutool里的QrCodeUtil
            // 网址：http://hutool.mydoc.io/
            QrCodeUtil.generate(uuid, 300, 300, "jpg", response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // http://localhost:8080/bind?token=gkl5hmzepiet31ise0snwsbddyf9wemo&userId=1
    @GetMapping(value = "/bind")
    public JSONObject bindUserIdAndToken(@RequestParam("token") String token, @RequestParam("userId") Integer userId) {
        try {
            String oldUserId = MAP.get(token);
            if (StringUtils.isNotBlank(oldUserId)) {
                MAP.put(token, oldUserId);
            } else {
                throw new Exception("二维码失效");
            }
            JSONObject msg = new JSONObject().fluentPut("code", "200").fluentPut("msg", userId);
            QrCodeWebSocketServer.sendInfo(JSONObject.toJSONString(msg), token);
            return msg;
        } catch (Exception e) {
            JSONObject msg = new JSONObject().fluentPut("code", "500").fluentPut("msg", e.getMessage());
            QrCodeWebSocketServer.sendInfo(JSONObject.toJSONString(msg), token);
            return msg;
        }

    }
}
