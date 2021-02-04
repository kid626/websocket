package com.bruce.websocket.util;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @Copyright Copyright © 2021 fanzh . All rights reserved.
 * @Desc
 * @ProjectName websocket
 * @Date 2021/2/4 21:53
 * @Author Bruce
 */
@Slf4j
public class UrlUtil {

    public static Map<String, String> getParameter(String url) {
        Map<String, String> map = new HashMap<>(16);
        String[] keyValues = url.split("&");
        for (String keyValue : keyValues) {
            String key = keyValue.substring(0, keyValue.indexOf("="));
            String value = keyValue.substring(keyValue.indexOf("=") + 1);
            map.put(key, value);
        }
        return map;
    }

}
