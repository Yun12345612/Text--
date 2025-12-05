package org.example.springboot_hd.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.springboot_hd.entity.Role;

import java.util.List;

@Mapper
public interface RoleMapper {

    //1.根据Id查询单个角色
    Role selectById(Long roleId);

    //1.1带条件查询
    List<Role> selectList(Role role);
    //1.2查询符合条件的角色总条数
    int selectCount(Role role);

    //2.添加角色
    int insert(Role role);

    //3.修改角色
    int update(Role role);

    //4.软删除角色
    int softDeleteById(Long roleId);
}