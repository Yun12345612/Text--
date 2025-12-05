package org.example.springboot_hd.service.impl;

import jakarta.annotation.Resource;
import org.example.springboot_hd.entity.Menu;
import org.example.springboot_hd.mapper.RoleMenuMapper;
import org.example.springboot_hd.service.RoleMenuService;
import org.example.springboot_hd.utils.MenuTreeBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @BelongsProject: springboot_hd
 * @BelongsPackage: org.example.springboot_hd.service.impl
 * @Author: 夏日花店
 * @CreateTime: 2025-10-28 10:28:47
 * @Description: 头部注释
 * @Version: 1.0
 */
@Service
public class RoleMenuServiceImpl implements RoleMenuService {

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Override
    public List<Menu> getMenusByRoleId(Integer roleId) {

        // 1. 调用Mapper查询该角色关联的所有菜单（平级列表）
        List<Menu> menus = roleMenuMapper.selectMenusByRoleId(roleId);
        System.out.println("菜单"+menus);
        // 2. 将平级菜单列表转换为树形结构（父菜单包含子菜单）
        return  MenuTreeBuilder.buildFullTree(menus);

    }
}

