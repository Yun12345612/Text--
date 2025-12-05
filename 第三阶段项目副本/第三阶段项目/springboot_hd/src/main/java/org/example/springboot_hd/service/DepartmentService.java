package org.example.springboot_hd.service;

import org.example.springboot_hd.entity.Department;

import java.util.List;

public interface DepartmentService {

    //1.根据Id查询单个科室
    Department getById(Long id);
    //1.1带条件查询
    List<Department> List(Department department);

    //2.添加科室
    int addDepartment(Department department);
    //3.修改科室
    int updateDepartment(Department department);
    //4.软删除科室
    int softDeleteById(Long id);


}
