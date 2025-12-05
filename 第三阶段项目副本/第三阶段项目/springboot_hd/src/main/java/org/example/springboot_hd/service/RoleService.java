package org.example.springboot_hd.service;

import org.example.springboot_hd.dto.ResponseDTO;
import org.example.springboot_hd.entity.Role;

import java.util.List;

public interface RoleService {

    // 1. 根据ID查询单个角色
    ResponseDTO selectById(Long roleId);

    // 2. 带条件查询角色列表（支持分页）
    ResponseDTO selectList(Role role);
    //2.1查询总条数
    ResponseDTO selectCount(Role role);

    // 3. 新增角色
    ResponseDTO insert(Role role);

    // 4. 修改角色
    ResponseDTO update(Role role);

    // 5. 软删除角色
    ResponseDTO softDeleteById(Long roleId);



}