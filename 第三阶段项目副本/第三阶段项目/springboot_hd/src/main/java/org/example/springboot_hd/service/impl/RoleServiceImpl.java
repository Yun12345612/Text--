package org.example.springboot_hd.service.impl;

import org.example.springboot_hd.dto.ResponseDTO;
import org.example.springboot_hd.entity.Role;
import org.example.springboot_hd.mapper.RoleMapper;
import org.example.springboot_hd.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper; // 注入角色Mapper

    @Override
    public ResponseDTO selectById(Long roleId) {
        // 校验参数
        if (roleId == null) {
            return ResponseDTO.error("角色ID不能为空");
        }
        // 调用Mapper查询
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            return ResponseDTO.error("未找到该角色");
        }
        return ResponseDTO.success(role, "查询成功");
    }

    @Override
    public ResponseDTO selectList(Role role) {
        // 调用Mapper查询列表（支持条件+分页）
        List<Role> roleList = roleMapper.selectList(role);
        // 若需要分页总数，可添加：int total = roleMapper.selectCount(role); 再封装到自定义分页对象中
        return ResponseDTO.success(roleList, "查询成功");
    }

    //总条数查询
    @Override
    public ResponseDTO selectCount(Role role) {
        // 调用Mapper的selectCount方法获取总条数
        int total = roleMapper.selectCount(role);
        return ResponseDTO.success(total, "总条数查询成功");
    }

    @Override
    public ResponseDTO insert(Role role) {
        // 参数校验（示例：角色名不能为空）
        if (role == null || role.getRoleName() == null || role.getRoleName().trim().isEmpty()) {
            return ResponseDTO.error("角色名称不能为空");
        }
        // 调用Mapper新增
        int result = roleMapper.insert(role);
        if (result > 0) {
            return ResponseDTO.success("角色新增成功");
        }
        return ResponseDTO.error("角色新增失败");
    }


    @Override
    public ResponseDTO update(Role role) {
        // 参数校验（ID和角色名不能为空）
        if (role == null || role.getRoleId() == null) {
            return ResponseDTO.error("角色ID不能为空");
        }
        if (role.getRoleName() == null || role.getRoleName().trim().isEmpty()) {
            return ResponseDTO.error("角色名称不能为空");
        }
        // 调用Mapper修改
        int result = roleMapper.update(role);
        if (result > 0) {
            return ResponseDTO.success("角色修改成功");
        }
        return ResponseDTO.error("角色修改失败或角色不存在");
    }

    @Override
    public ResponseDTO softDeleteById(Long roleId) {
        // 校验参数
        if (roleId == null) {
            return ResponseDTO.error("角色ID不能为空");
        }
        // 调用Mapper软删除
        int result = roleMapper.softDeleteById(roleId);
        if (result > 0) {
            return ResponseDTO.success("角色删除成功");
        }
        return ResponseDTO.error("角色删除失败或角色不存在");
    }


}