package org.example.springboot_hd.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.springboot_hd.entity.Department;

import java.util.List;

@Mapper
public interface DepartmentMapper {

    //1.根据Id查询单个科室
    Department selectById(Long id);
    //1.1带条件查询
    List<Department> selectList(Department department);

    //2.添加科室
    int insert(Department department);
    //3.修改科室
    int update(Department department);
    //4.软删除科室
    int softDeleteById(Long id);



}

