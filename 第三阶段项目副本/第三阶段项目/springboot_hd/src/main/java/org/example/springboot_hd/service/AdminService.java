package org.example.springboot_hd.service;

import org.example.springboot_hd.dto.ResponseDTO;
import org.example.springboot_hd.entity.Admin;

public interface AdminService {
    //1.管理员登录
    ResponseDTO login(String account, String password);
    //2.管理员注册
    ResponseDTO registerAdmin(Admin admin);
    //3.管理员修改
    ResponseDTO updateAdmin(Admin admin);
    //4.管理员查询
    ResponseDTO selectAdminList(Admin admin);
    //5.软删除管理员
    ResponseDTO softDeleteAdmin(Long id);
    //6.新增管理员
    ResponseDTO addAdmin(Admin admin);

}
