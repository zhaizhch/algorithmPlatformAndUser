package com.example.demo.utils;

import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class CommonUtils {
    public static String encodeByMd5(String str) {
        return DigestUtils.md5DigestAsHex(str.getBytes(StandardCharsets.UTF_8));
    }

    public static String getRandomStr() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
