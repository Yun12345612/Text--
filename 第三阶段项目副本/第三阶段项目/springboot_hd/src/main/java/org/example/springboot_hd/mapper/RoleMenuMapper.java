package org.example.springboot_hd.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.springboot_hd.entity.Menu;

import java.util.List;

@Mapper
public interface RoleMenuMapper {

    //1.根据角色id查询关联的菜单
    List<Menu> selectMenusByRoleId(@Param("roleId") Integer roleId);
}
