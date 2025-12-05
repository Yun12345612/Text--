package com.cykj.controller;

import com.cykj.pojo.SystemInfo;
import com.cykj.service.SystemInfoService;
import com.cykj.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: 夏日花店
 * @CreateTime: 2025-10-18 11:16:34
 * @Description: 头部注释
 * @Version: 1.0
 */
@RestController
@RequestMapping("/system")
public class SystemInfoController {

    // 注入Service层对象
    private final SystemInfoService systemInfoService;

    /**
     * 构造方法注入Service
     *
     * @param systemInfoService 系统信息服务层对象
     */
    @Autowired // Spring自动注入Service实例
    public SystemInfoController(SystemInfoService systemInfoService) {
        this.systemInfoService = systemInfoService;
    }

    /**
     * 处理获取系统信息的GET请求
     *
     * @return 系统信息实体类
     */
    @GetMapping("/info")
    public SystemInfo getSystemInfo(HttpServletRequest request) {
        //从 HTTP 请求头的 X-Token 字段中获取 JWT token（前端需要将登录时获得的 token 放在该请求头中）。
        String token = request.getHeader("X-Token");

        try {
            Claims claims = JwtUtils.parseJWT(token);
            int userId = Integer.parseInt(claims.get("userId").toString());
            return systemInfoService.getSystemInfo();
        } catch (ExpiredJwtException e) {
            System.out.println(e.getMessage());
            // 捕获token过期异常
            System.out.println("token已过期");
            // 可以在这里进行相应的处理，比如返回提示信息给前端
            throw new RuntimeException("token已过期，请重新登录");
        } catch (Exception e) {
            // 捕获其他异常
            System.out.println("解析token出现其他错误：" + e.getMessage());
            throw new RuntimeException("解析token失败");
        }
    }
}
