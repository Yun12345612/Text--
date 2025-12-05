package org.example.springboot_hd.controller;

import org.example.springboot_hd.dto.ResponseDTO;
import org.example.springboot_hd.entity.User;
import org.example.springboot_hd.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @BelongsProject: springboot_hd
 * @BelongsPackage: org.example.springboot_hd.controller
 * @Author: 夏日花店
 * @CreateTime: 2025-11-02 01:13:04
 * @Description: 头部注释
 * @Version: 1.0
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    //1.查询用户列表
    @GetMapping("/getUserList")
    public ResponseDTO listUser(@ModelAttribute User user) {
        return userService.selectUserList(user);
    }

    //2.新增用户
    @PostMapping("/adduser")
    public ResponseDTO addUser(@RequestBody User user) {
        return userService.insertUser(user);
    }

    //3.更新用户信息
    @PutMapping("/updateuser")
    public ResponseDTO updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    //4.软删除用户
    @DeleteMapping("/delete/{userId}")
    public ResponseDTO delete(@PathVariable Long userId) {
        return userService.softDeleteUser(userId);
    }
}
