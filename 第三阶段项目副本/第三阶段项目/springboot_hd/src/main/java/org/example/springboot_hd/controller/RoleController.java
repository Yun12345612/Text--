package org.example.springboot_hd.controller;

import org.example.springboot_hd.dto.ResponseDTO;
import org.example.springboot_hd.entity.Role;
import org.example.springboot_hd.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 角色管理控制层（与科室管理风格对齐）
 */
@RestController
@RequestMapping("/role") // 统一接口前缀
public class RoleController {
    @Autowired
    private RoleService roleService;

    /**
     * 1. 根据ID查询单个角色
     * 请求方式：GET
     * 示例路径：/role/1
     */
    @GetMapping("/{roleId}")
    public ResponseDTO getRoleById(@PathVariable Long roleId) {
        // 直接调用Service层方法（Service已做参数校验和结果封装）
        return roleService.selectById(roleId);
    }

    /**
     * 2. 带条件查询角色列表（支持分页）
     * 请求方式：GET
     * 示例路径：/role/list?roleName=管理员&currentPage=1&pageSize=10
     * 说明：分页参数显式接收，计算偏移量后设置到Role对象传递给Service
     */
    @GetMapping("/getrolelist")
    public ResponseDTO getRoleList(
            @RequestParam(required = false) Integer pageNum,  // 直接接收前端传递的偏移量
            @RequestParam(required = false) Integer pageSize,
            Role role) {
        try {
            // 补充默认分页参数（防止前端未传递时的空指针）
            if (pageNum == null || pageNum < 0) {
                pageNum = 0; // 默认从第0条开始
            }
            if (pageSize == null || pageSize <= 0) {
                pageSize = 10; // 默认每页10条
            }
            // 设置分页参数到role对象
            role.setPageNum(pageNum);
            role.setPageSize(pageSize);

            // 调用Service查询
            ResponseDTO listResponse = roleService.selectList(role);
            ResponseDTO countResponse = roleService.selectCount(role);

            // 校验响应
            if (listResponse.getCode() != 200) {
                return ResponseDTO.error(500, "角色列表查询失败: " + listResponse.getMsg());
            }
            if (countResponse.getCode() != 200) {
                return ResponseDTO.error(500, "总条数查询失败: " + countResponse.getMsg());
            }

            // 封装响应
            List<Role> roleList = (List<Role>) listResponse.getData();
            int total = (Integer) countResponse.getData();
            Map<String, Object> pageData = new HashMap<>();
            pageData.put("list", roleList);
            pageData.put("total", total);
            pageData.put("currentPage", (pageNum / pageSize) + 1); // 计算当前页返回给前端
            pageData.put("pageSize", pageSize);

            return ResponseDTO.success(pageData, "查询成功");
        } catch (Exception e) {
            return ResponseDTO.error(500, "查询失败: " + e.getMessage());
        }
    }

    /**
     * 3. 新增角色
     * 请求方式：POST
     * 示例路径：/role/add
     * 请求体：{"roleName":"测试角色","roleDesc":"测试描述","roleStatus":1}
     */
    @PostMapping("/addrolelist")
    public ResponseDTO addRole(@RequestBody Role role) {
        // 直接调用Service层方法（Service已做参数校验和结果封装）
        return roleService.insert(role);
    }

    /**
     * 4. 修改角色
     * 请求方式：POST
     * 示例路径：/role/update
     * 请求体：{"roleId":1,"roleName":"更新角色","roleStatus":0,"roleDesc":"更新描述"}
     */
    @PostMapping("/updaterolelist")
    public ResponseDTO updateRole(@RequestBody Role role) {
        // 直接调用Service层方法（Service已做ID校验和结果封装）
        return roleService.update(role);
    }

    /**
     * 5. 软删除角色
     * 请求方式：DELETE
     * 示例路径：/role/softDelete/1
     */
    @DeleteMapping("/softDelete/{roleId}")
    public ResponseDTO softDeleteRole(@PathVariable Long roleId) {
        // 直接调用Service层方法（Service已做ID校验和结果封装）
        return roleService.softDeleteById(roleId);
    }
}