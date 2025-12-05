package org.example.springboot_hd.service;

import jakarta.servlet.http.HttpServletResponse;
import org.example.springboot_hd.dto.ResponseDTO;
import org.example.springboot_hd.entity.User;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    //1.查询用户列表
    ResponseDTO selectUserList(User user);

    //2.新增用户
    ResponseDTO insertUser(User user);

    //3.更新用户信息
    ResponseDTO updateUser(User user);

    //4.软删除用户
    ResponseDTO softDeleteUser(Long userid);

}
