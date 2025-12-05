package org.example.springboot_hd.controller;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.springboot_hd.entity.SystemInfo;
import org.example.springboot_hd.service.SystemInfoService;
import org.example.springboot_hd.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

/**
 * @BelongsProject: springboot_hd
 * @BelongsPackage: org.example.springboot_hd.controller
 * @Author: 夏日花店
 * @CreateTime: 2025-10-29 09:11:59
 * @Description: 头部注释
 * @Version: 1.0
 */
@RestController
@RequestMapping("/system")
public class SystemInfoController {
    @Autowired
    private SystemInfoService systemInfoService;
    @Autowired // Spring自动注入Service实例
    public SystemInfoController(SystemInfoService systemInfoService) {
        this.systemInfoService = systemInfoService;
    }

    @GetMapping("/info")
    public SystemInfo getSystemInfo(HttpServletRequest request) {
        String token = request.getHeader("X-Token");
        try {
            // 仅当 Token 存在时才解析（可选逻辑，如记录用户操作日志）
            if (token != null && !token.isEmpty()) {
                Claims claims = JwtUtils.parseJWT(token);
                int adminId = Integer.parseInt(claims.get("adminId").toString());
                // 此处可添加基于 adminId 的业务逻辑（如日志记录）
            }
            // 直接返回系统信息，不依赖 Token 校验
            return systemInfoService.getSystemInfo();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("token已过期，请重新登录");
        } catch (Exception e) {
            // Token 解析失败时，不抛出异常，直接返回系统信息
            System.out.println("解析token出现其他错误：" + e.getMessage());
            return systemInfoService.getSystemInfo();
        }
    }
}

