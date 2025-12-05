package com.cykj.pojo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: 夏日花店
 * @CreateTime: 2025-10-14 21:38:43
 * @Description: 头部注释
 * @Version: 1.0
 */
@Data
public class CyMenu {
    private Integer menuId;        // 菜单ID（对应 menu_id）
    private String menuName;      // 菜单名称（对应 menu_name）
    private String menuComponent; // 前端组件路径（对应 menu_component）
    private String menuPath;      // 路由路径（对应 menu_path）
    private String menuIcon;      // 菜单图标（对应 menu_icon）
    private Integer menuParentId; // 父菜单ID（对应 menu_parent_id）
    private Integer menuStatus;   // 菜单状态（1启用/0禁用，对应 menu_status）
    // 子菜单（用于组装树形结构）
    private List<CyMenu> children = new ArrayList<>();
}
