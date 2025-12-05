package com.cykj.mapper;

import com.cykj.pojo.CyAdmin;
import com.cykj.pojo.User;
import org.mybatis.spring.annotation.MapperScan;


public interface UserMapper {
    //1.登录加密
    User getUserByAccountOrPhoneOrCard(String loginInput);
    //2.用户注册
    int insertUser(User user);
    //3.注册验证 - 根据账号查询
    User getUserByAccount(String userAccount);
    //4.注册验证 - 根据手机号查询
    User getUserByPhone(String userPhone);
    //5.注册验证 - 根据身份证号查询
    User getUserByCard(String userCard);
}
