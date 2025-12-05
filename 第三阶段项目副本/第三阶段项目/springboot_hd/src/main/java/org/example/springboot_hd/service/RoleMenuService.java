package org.example.springboot_hd.service;

import org.apache.ibatis.annotations.Param;
import org.example.springboot_hd.entity.Menu;

import java.util.List;

public interface RoleMenuService {
    /**
     * 根据角色ID查询关联的菜单列表（树形结构）
     * @param roleId 角色ID
     * @return 菜单树形列表
     */
    List<Menu> getMenusByRoleId(Integer roleId);

}
