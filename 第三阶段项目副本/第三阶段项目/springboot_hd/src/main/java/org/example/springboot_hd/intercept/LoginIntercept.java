package org.example.springboot_hd.intercept;

import com.alibaba.fastjson2.JSON;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.springboot_hd.dto.ResponseDTO;
import org.example.springboot_hd.utils.JwtUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Enumeration;

/**
 * @BelongsProject: springboot_hd
 * @BelongsPackage: org.example.springboot_hd.intercept
 * @Author: 夏日花店
 * @CreateTime: 2025-10-28 09:54:32
 * @Description: 登录拦截器（修复通配符匹配问题）
 * @Version: 1.0
 */
public class LoginIntercept implements HandlerInterceptor {

    // 白名单：无需登录即可访问的路径
    private static final String[] WHITE_LIST = {
            "/admin/login",
            "/admin/register",
            "/tree/menus",
            "/get/code",
            "/system/info",
            "/static/**",
            "/department/**","/examination-item-detail/all"
    };

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String url = request.getRequestURI();
        System.out.println("当前请求路径：" + url);

        // 检查是否在白名单内
        for (String pattern : WHITE_LIST) {
            if (matches(url, pattern)) {
                System.out.println("白名单放行: " + url);
                return true;
            }
        }
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            System.out.println("请求头: " + name + " = " + request.getHeader(name));
        }
        // 非白名单路径：校验Token
        String token = request.getHeader("Authorization");
        if (token == null || token.trim().isEmpty()) {
            ResponseDTO dto = ResponseDTO.error(-1, "请先登录");
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(JSON.toJSONString(dto));
            return false;
        }

        try {
            // 验证Token并提取用户信息
            Claims claims = JwtUtils.parseJWT(token);
            request.setAttribute("adminId", claims.get("adminId"));
            return true;
        } catch (Exception e) {
            // Token无效或过期
            ResponseDTO dto = ResponseDTO.error(-2, "登录已过期，请重新登录");
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(JSON.toJSONString(dto));
            return false;
        }
    }

    /**
     * 通配符匹配工具（修复**匹配逻辑）
     * 支持：
     * - **：匹配任意路径（包括多级子路径）
     * - *：匹配单级路径（不包含斜杠）
     * - 精确匹配：完全相等的路径
     */
    private boolean matches(String url, String pattern) {
        // 处理**通配符（匹配任意路径）
        if (pattern.contains("**")) {
            String prefix = pattern.split("\\*\\*")[0];
            // 只要URL以prefix开头即可（例如/department/**匹配所有/department/开头的路径）
            return url.startsWith(prefix);
        }
        // 处理*通配符（匹配单级路径）
        else if (pattern.contains("*")) {
            // 将*替换为正则表达式（匹配非斜杠的任意字符）
            String regex = pattern.replace("*", "[^/]*");
            // 加上^和$确保完全匹配
            return url.matches("^" + regex + "$");
        }
        // 精确匹配
        else {
            return url.equals(pattern);
        }
    }
}