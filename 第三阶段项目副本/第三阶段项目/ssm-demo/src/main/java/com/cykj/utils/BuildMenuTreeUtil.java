package com.cykj.utils;

import com.cykj.pojo.CyMenu;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class BuildMenuTreeUtil {
    // 对外提供的公共静态方法，用于构建菜单树
    public static List<CyMenu> build(List<CyMenu> allMenus, int parentId) {
        return buildMenuTree(allMenus, parentId);
    }

    // 递归构建菜单树的内部方法
    private static List<CyMenu> buildMenuTree(List<CyMenu> allMenus, int parentId) {
        List<CyMenu> children = allMenus.stream()
                .filter(menu -> {
                    Integer menuParentId = menu.getMenuParentId();
                    // 处理menuParentId为null的情况（根菜单）
                    return Objects.equals(menuParentId == null ? 0 : menuParentId, parentId);
                })
                .collect(Collectors.toList());

        if (!children.isEmpty()) {
            for (CyMenu child : children) {
                // 递归设置子菜单
                child.setChildren(buildMenuTree(allMenus, child.getMenuId()));
            }
        }
        return children;
    }
}