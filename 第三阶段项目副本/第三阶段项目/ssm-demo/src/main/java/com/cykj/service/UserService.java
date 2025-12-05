package com.cykj.service;

import com.cykj.dto.ResponseDTO;
import com.cykj.pojo.User;

public interface UserService {
    //1.用户登录
    ResponseDTO login(String loginInput, String pwd);
    //2.用户注册
    ResponseDTO register(User user);

}
