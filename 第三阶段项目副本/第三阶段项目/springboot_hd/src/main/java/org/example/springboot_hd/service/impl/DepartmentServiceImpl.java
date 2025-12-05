package org.example.springboot_hd.service.impl;

import org.example.springboot_hd.entity.Department;
import org.example.springboot_hd.mapper.DepartmentMapper;
import org.example.springboot_hd.service.DepartmentService;
import org.example.springboot_hd.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @BelongsProject: springboot_hd
 * @BelongsPackage: org.example.springboot_hd.service.impl
 * @Author: 夏日花店
 * @CreateTime: 2025-10-28 16:38:58
 * @Description: 头部注释
 * @Version: 1.0
 */
@Service
public class DepartmentServiceImpl implements DepartmentService {
    @Autowired
    private DepartmentMapper departmentMapper;

    //1.根据ID查询单个科室
    @Override
    public Department getById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("科室ID不能为空");
        }
        return departmentMapper.selectById(id);
    }

    //1.1带条件查询
    @Override
    public List<Department> List(Department department) {
        return departmentMapper.selectList(department);
    }

    //2.添加科室
    @Override
    public int addDepartment(Department department) {
        // 参数校验：如果科室对象为null，直接返回0（插入失败）
        if (department == null) {
            return 0;
        }
        return departmentMapper.insert(department);
    }

    //3.修改科室
    @Override
    public int updateDepartment(Department department) {
        if (department == null || department.getDepartmentId() == null) {
            return 0;
        }
        return departmentMapper.update(department);
    }

    //4.软删除科室
    @Override
    public int softDeleteById(Long id) {
        if (id == null) {
            return 0;
        }
        return departmentMapper.softDeleteById(id);
    }
}
