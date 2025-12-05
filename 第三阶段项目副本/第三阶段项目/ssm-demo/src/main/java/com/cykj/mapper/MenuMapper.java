package com.cykj.mapper;

import com.cykj.pojo.CyMenu;
import com.cykj.pojo.CyRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MenuMapper {

    // 根据角色ID查询该角色可访问的菜单
    List<CyMenu> selectMenusByRoleId(@Param("roleId") Integer roleId);

}