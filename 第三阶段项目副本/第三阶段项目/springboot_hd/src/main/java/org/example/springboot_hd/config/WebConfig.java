package org.example.springboot_hd.config;

import org.example.springboot_hd.intercept.LoginIntercept;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @BelongsProject: springboot_hd
 * @BelongsPackage: org.example.springboot_hd.config
 * @Author: 夏日花店
 * @CreateTime: 2025-10-28 10:59:11
 * @Description: Spring MVC 的拦截器需要通过 WebMvcConfigurer 配置类来注册，才能让 Spring 识别并生效
 * @Version: 1.0
 */
@Configuration
@EnableTransactionManagement
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 LoginIntercept 拦截器，并配置拦截规则
        registry.addInterceptor(new LoginIntercept())
                .addPathPatterns("/**")  // 拦截所有请求
                .excludePathPatterns( "/admin/login",
                        "/admin/register",
                        "/tree/menus",
                        "/get/code",
                        "/system/info",
                        "/static/**"); // 排除白名单请求

    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将 URL 路径 "/static/**" 映射到类路径下的 "/static/" 目录
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
        // 你也可以添加其他映射
    }
}