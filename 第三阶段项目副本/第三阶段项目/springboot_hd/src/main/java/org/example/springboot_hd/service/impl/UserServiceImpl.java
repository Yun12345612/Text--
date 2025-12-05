package org.example.springboot_hd.service.impl;

import io.micrometer.common.util.StringUtils;
import org.example.springboot_hd.dto.PageResultDTO;
import org.example.springboot_hd.dto.ResponseDTO;
import org.example.springboot_hd.entity.Admin;
import org.example.springboot_hd.entity.User;
import org.example.springboot_hd.mapper.UserMapper;
import org.example.springboot_hd.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @BelongsProject: springboot_hd
 * @BelongsPackage: org.example.springboot_hd.service.impl
 * @Author: 夏日花店
 * @CreateTime: 2025-11-02 00:56:36
 * @Description: 头部注释
 * @Version: 1.0
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    //1.查询用户列表
    @Override
    public ResponseDTO selectUserList(User user) {
        int pageNum = Optional.ofNullable(user.getPageNum()).orElse(1);
        int pageSize = Optional.ofNullable(user.getPageSize()).orElse(1);
        user.setStart((pageNum - 1) * pageSize);
        //2.调用Mapper查询
        List<User> userList = userMapper.selectUserList(user);
        int total = userMapper.selectUserCount(user);
        //3.封装分页结果
        PageResultDTO<User> pageResult = new PageResultDTO<>();
        pageResult.setList(userList);
        pageResult.setTotal(total);
        pageResult.setPageNum(pageNum);
        pageResult.setPageSize(pageSize);
        pageResult.setTotalPage((total + pageSize - 1) / pageSize);
        return ResponseDTO.success(pageResult);

    }

    //2.新增用户
    @Override
    public ResponseDTO insertUser(User user) {
        // 1. 校验手机号是否已存在
        try {
            if (StringUtils.isNotBlank(user.getUserPhone())) {
                User queryUser = new User();
                queryUser.setUserPhone(user.getUserPhone());
                List<User> existingUsers = userMapper.selectUserList(queryUser);
                if (existingUsers != null && !existingUsers.isEmpty()) {
                    return ResponseDTO.error("手机号已存在");
                }
            }

            // 2. 校验身份证号是否已存在
            if (StringUtils.isNotBlank(user.getUserCard())) {
                User queryUser = new User();
                queryUser.setUserCard(user.getUserCard());
                List<User> existingUsers = userMapper.selectUserList(queryUser);
                if (existingUsers != null && !existingUsers.isEmpty()) {
                    return ResponseDTO.error("身份证号已存在");
                }
            }

            // 3. 校验邮箱是否已存在
            if (StringUtils.isNotBlank(user.getUserEmail())) {
                User queryUser = new User();
                queryUser.setUserEmail(user.getUserEmail());
                List<User> existingUsers = userMapper.selectUserList(queryUser);
                if (existingUsers != null && !existingUsers.isEmpty()) {
                    return ResponseDTO.error("邮箱已存在");
                }
            }
            //4.执行插入
            int result = userMapper.insertUser(user);
            if (result > 0) {
                return ResponseDTO.success("用户添加成功");
            } else {
                return ResponseDTO.error("用户添加失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDTO.error("系统异常：" + e.getMessage());
        }
    }

    //3.更新用户信息
    @Override
    public ResponseDTO updateUser(User user) {
        //执行插入
        int result = userMapper.updateUser(user);
        if (result > 0) {
            return ResponseDTO.success("用户编辑成功");
        } else {
            return ResponseDTO.error("用户编辑失败");
        }
    }

    //4.软删除用户
    @Override
    public ResponseDTO softDeleteUser(Long userid) {
        int result = userMapper.softDeleteUser(userid);
        if (result > 0) {
            return ResponseDTO.success("软删除成功");
        } else {
            return ResponseDTO.error("软删除失败");
        }
    }
}