package org.example.springboot_hd.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.springboot_hd.entity.User;

import java.util.List;

@Mapper
public interface UserMapper {
    //1.查询用户列表
    List<User> selectUserList(User user);

    //2.查询总数
    int selectUserCount(User user);

    //3.新增用户
    int insertUser(User user);

    //4.更新用户信息
    int updateUser(User user);

    //5.软删除用户
    int softDeleteUser(@Param("user_id") Long userid);


}

