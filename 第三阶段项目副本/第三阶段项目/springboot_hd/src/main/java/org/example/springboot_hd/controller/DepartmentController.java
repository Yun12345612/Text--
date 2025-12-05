package org.example.springboot_hd.controller;

import org.example.springboot_hd.dto.ResponseDTO;
import org.example.springboot_hd.entity.Department;
import org.example.springboot_hd.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @BelongsProject: springboot_hd
 * @BelongsPackage: org.example.springboot_hd.controller
 * @Author: 夏日花店
 * @CreateTime: 2025-10-28 16:55:38
 * @Description: 头部注释
 * @Version: 1.0
 */
@RestController
@RequestMapping("/department")
public class DepartmentController {
    @Autowired
    private DepartmentService departmentService;

    //1.根据Id查询单个科室
    @GetMapping("/{id}")
    public ResponseDTO getDepartment(@PathVariable Long id) {
        Department department = departmentService.getById(id);
        if (department == null) {
            return ResponseDTO.error(400, "科室ID无效");
        }
        return ResponseDTO.success("查询成功");
    }

    //1.1带条件的查询科室
    // 使用Map接收所有参数，手动处理类型转换
    @GetMapping("/gteList")
    public ResponseDTO getAllDepartments(
            @RequestParam Map<String, Object> params) {
        try {
            Department department = new Department();

            // 处理状态参数
            if (params.containsKey("departmentStatus") && params.get("departmentStatus") != null) {
                String statusStr = params.get("departmentStatus").toString();
                if (!statusStr.isEmpty()) {
                    department.setDepartmentStatus(Integer.parseInt(statusStr));
                }
            }

            // 处理科室名称
            if (params.containsKey("departmentName") && params.get("departmentName") != null) {
                department.setDepartmentName(params.get("departmentName").toString());
            }

            // 分页参数处理
            Integer currentPage = 1;
            Integer pageSize = 10;

            if (params.containsKey("currentPage") && params.get("currentPage") != null) {
                currentPage = Integer.parseInt(params.get("currentPage").toString());
            }
            if (params.containsKey("pageSize") && params.get("pageSize") != null) {
                pageSize = Integer.parseInt(params.get("pageSize").toString());
            }

            if (currentPage < 1) currentPage = 1;
            if (pageSize <= 0) pageSize = 10;

            int offset = (currentPage - 1) * pageSize;
            department.setPageNum(offset);
            department.setPageSize(pageSize);

            List<Department> departments = departmentService.List(department);

            Map<String, Object> pageData = new HashMap<>();
            pageData.put("list", departments);
            pageData.put("total", departments.size());
            pageData.put("currentPage", currentPage);
            pageData.put("pageSize", pageSize);

            return ResponseDTO.success(pageData,"查询成功");
        } catch (Exception e) {
            return ResponseDTO.error(500, "查询失败: " + e.getMessage());
        }
    }
    //2.添加科室
    @PostMapping("/add")
    public ResponseDTO addDepartment(@RequestBody Department department) {
        //调用Service层方法
        int result = departmentService.addDepartment(department);
        if (result > 0) {
            return ResponseDTO.success(department,"添加成功");//添加成功
        } else {
            return ResponseDTO.error(400, "添加科室失败，请检查参数是否完整或正确");//添加失败
        }
    }

    //3.修改科室
    @PostMapping("/update")
    public ResponseDTO updateDepartment(@RequestBody Department department) {
        if (department == null || department.getDepartmentId() == null) {
            return ResponseDTO.error(400, "修改失败:科室Id不能为空");
        }
        int result = departmentService.updateDepartment(department);
        if (result > 0) {
            return ResponseDTO.success( department,"修改成功");
        } else {
            return ResponseDTO.error(400, "修改失败:未找到该科室或有无效更新内容");
        }
    }

    //4.软删除科室
    @DeleteMapping("/softDelete/{id}")
    public ResponseDTO softDeleteDepartment(@PathVariable Long id) {
        // 1. 前置参数校验（快速失败）
        if (id == null || id <= 0) {
            return ResponseDTO.error(400, "删除失败：科室ID无效（不能为空或负数）");
        }
        // 2. 调用Service层执行软删除
        int result = departmentService.softDeleteById(id);
        // 3. 根据结果返回响应
        if (result > 0) {
            // 成功
            return ResponseDTO.success("科室已成功标记为删除");
        } else {
            // 失败
            return ResponseDTO.error(404, "删除失败：未找到该科室或科室已被删除");
        }
    }
}
