package com.cykj.controller;

import com.cykj.dto.ResponseDTO;
import com.cykj.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 菜单控制器：处理与权限菜单相关的HTTP请求
 */
@RestController
@RequestMapping("/menu")
public class MenuController {

    // 注入菜单服务
    @Autowired
    private MenuService menuService;

    @GetMapping("/permission-tree")
    public ResponseDTO getPermissionMenuTree(
            @RequestParam("adminId") String adminId // 接收前端传入的管理员ID
    ) {
        // 直接调用Service层方法
        return menuService.getPermissionMenu(adminId);
    }
}
