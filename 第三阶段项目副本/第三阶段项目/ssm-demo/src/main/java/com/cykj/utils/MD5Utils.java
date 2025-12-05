package com.cykj.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class MD5Utils {
    // 生成随机盐值
    public static String generateSalt() {
        Random random = new Random();
        StringBuilder salt = new StringBuilder();
        // 生成8位随机盐值
        for (int i = 0; i < 8; i++) {
            salt.append(random.nextInt(10));
        }
        return salt.toString();
    }

    // MD5加密(带盐值)
    public static String encrypt(String password, String salt) {
        // 新增：处理salt为null或空字符串的情况，避免异常拼接
        if (salt == null || salt.trim().isEmpty()) {
            throw new IllegalArgumentException("盐值(salt)不能为空或null");
        }
        // 新增：处理password为null的情况（避免NullPointerException）
        if (password == null) {
            password = "";
        }

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 拼接密码和盐值（此时salt已确保非空）
            byte[] bytes = md.digest((password + salt).getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    sb.append('0');
                }
                sb.append(hex);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5加密失败", e);
        }
    }
}