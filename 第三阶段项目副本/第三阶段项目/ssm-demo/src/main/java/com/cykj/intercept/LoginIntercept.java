package com.cykj.intercept;

import com.alibaba.fastjson.JSON;
import com.cykj.dto.ResponseDTO;
import com.cykj.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: 夏日花店
 * @CreateTime: 2025-10-17 10:57:36
 * @Description: 拦截器
 * @Version: 1.0
 */
public class LoginIntercept implements HandlerInterceptor {
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 白名单：与截取后的realUrl匹配（无需加/api前缀）
        String[] whiteList =
                {"/code", "/admin/login", "/card/import", "/card/importCards", "/alipay/",
                        "/user/login", "/user/register",
                        "/system/info", "/order/"};
        String url = request.getRequestURI();
        System.out.println("当前请求完整路径: " + url); // 可查看实际请求路径

        // 关键：截取上下文路径/api，得到真实业务路径
        String realUrl = url.replaceFirst("/api", "");
        System.out.println("截取后业务路径: " + realUrl); // 确认截取是否正确

        // 前缀匹配白名单：避免部分路径误匹配
        for (String white : whiteList) {
            if (realUrl.startsWith(white)) {
                System.out.println("白名单放行: " + url);
                return true;
            }
        }

        // Token校验：无Token返回“请先登录”
        String token = request.getHeader("X-Token");
        if (token == null || token.trim().equals("")) { // 加trim()，避免空字符串干扰
            ResponseDTO dto = ResponseDTO.error(-1, "请先登录");
            response.setContentType("application/json;charset=UTF-8"); //指定响应编码，避免乱码
            response.getWriter().write(JSON.toJSONString(dto));
            return false;
        }

        // Token解析：过期返回“登录过期”
        try {
            Claims claims = JwtUtils.parseJWT(token);
            request.setAttribute("adminId", claims.get("adminId"));
            return true;
        } catch (Exception e) {
            ResponseDTO dto = ResponseDTO.error(-2, "登录已过期，请重新登录");
            response.setContentType("application/json;charset=UTF-8"); // 指定响应编码
            response.getWriter().write(JSON.toJSONString(dto));
            return false;
        }
    }
}