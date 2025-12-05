package com.cykj.pojo;

import lombok.Data;

import java.util.List;

/**
 * @Author: 夏日花店
 * @CreateTime: 2025-10-14 21:45:24
 * @Description: 头部注释
 * @Version: 1.0
 */
@Data
public class CyRole {
    private Integer roleId;       // 角色ID（对应 role_id）
    private String roleName;      // 角色名称（对应 role_name）
    // 角色关联的菜单列表
    private List<CyMenu> menuList;
}
