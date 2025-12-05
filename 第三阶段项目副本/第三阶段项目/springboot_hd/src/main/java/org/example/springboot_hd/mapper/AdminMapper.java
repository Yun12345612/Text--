package org.example.springboot_hd.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.springboot_hd.entity.Admin;

import java.util.List;

@Mapper
public interface AdminMapper {
    //1.管理员登录
    Admin getAdminByAccount(String account);

    //2.管理员注册
    int insertAdmin(Admin admin);

    //3.修改管理员
    int updateAdmin(Admin admin);

    //4.管理员查询
    //查询管理员列表（带条件和分页）
    List<Admin> selectAdminList(Admin admin);
    //查询符合条件的总条数
    int selectAdminCount(Admin admin);

    //5.软删除管理员
    int softDeleteAdmin(@Param("id") Long id);
    //6.新增管理员
    int addAdmin(Admin admin);
}

