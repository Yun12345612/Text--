package org.example.springboot_hd.controller;

import org.example.springboot_hd.entity.Menu;
import org.example.springboot_hd.mapper.RoleMenuMapper;
import org.example.springboot_hd.service.RoleMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @BelongsProject: springboot_hd
 * @BelongsPackage: org.example.springboot_hd.controller
 * @Author: 夏日花店
 * @CreateTime: 2025-10-28 10:38:59
 * @Description: 权限菜单控制层
 * @Version: 1.0
 */
@RestController
@RequestMapping("/tree")
public class RoleMenuController {

    @Autowired
    private RoleMenuService roleMenuService;

    @GetMapping("/menus")
    public List<Menu> getRoleMenus(@RequestParam Integer roleId){
        return roleMenuService.getMenusByRoleId(roleId);
    }
}
