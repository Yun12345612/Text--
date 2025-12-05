package org.example.springboot_hd.utils;

import org.example.springboot_hd.entity.Menu;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
/**
 * @BelongsProject: springboot_hd
 * @BelongsPackage: org.example.springboot_hd.utils
 * @Author: 夏日花店
 * @CreateTime: 2025-10-27 15:58:54
 * @Description: 树形菜单工具类
 * @Version: 1.0
 */

public class MenuTreeBuilder {

    // 对外提供的公共静态方法，用于构建菜单树
    public static List<Menu> build(List<Menu> allMenus, Long parentId) {
        return buildMenuTree(allMenus, parentId);
    }
    // 递归构建菜单树的内部方法
    private static List<Menu> buildMenuTree(List<Menu> allMenus, Long parentId) {
        List<Menu> children = allMenus.stream()
                .filter(menu -> {
                    Long menuParentId = menu.getParentId();
                    // 处理menuParentId为null的情况（根菜单）
                    return Objects.equals(menuParentId == null ? 0L : menuParentId, parentId);
                })
                .collect(Collectors.toList());

        if (!children.isEmpty()) {
            for (Menu child : children) {
                // 递归设置子菜单
                child.setChildren(buildMenuTree(allMenus, child.getId()));
            }
        }
        return children;
    }

    /**
     * 构建完整的菜单树（从根节点开始）
     * 替代原来的buildTree方法
     */
    public static List<Menu> buildFullTree(List<Menu> allMenus) {
        return build(allMenus, 0L);
    }
}