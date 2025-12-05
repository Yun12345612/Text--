package com.cykj.controller;

import com.cykj.dto.ResponseDTO;
import com.cykj.pojo.CyAdmin;
import com.cykj.pojo.User;
import com.cykj.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * @BelongsProject: ssm-demo
 * @BelongsPackage: com.cykj.controller
 * @Author: 夏日花店
 * @CreateTime: 2025-11-03 13:44:29
 * @Description: 头部注释
 * @Version: 1.0
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;


    //1.登录
    @PostMapping("/login")
    public ResponseDTO login(@RequestBody User user, HttpSession session) {
        // 获取前端传入的验证码
        String inputCode = user.getCode();
        // 从session中获取存储的验证码
        String sessionCode = (String) session.getAttribute("code");
        // 验证码验证
        if (sessionCode == null) {
            return new ResponseDTO(400, "验证码已过期，请刷新重试", null);
        }
        if (!sessionCode.equalsIgnoreCase(inputCode)) {
            return new ResponseDTO(400, "验证码错误", null);
        }
        // 验证成功后清除session中的验证码（一次性使用）
        session.removeAttribute("code");
        // 获取账号密码进行登录
        String loginInput = user.getUserAccount();
        String pwd = user.getUserPassword();
        return userService.login(loginInput, pwd);
    }

    //2.用户注册
    @PostMapping("/register")
    public ResponseDTO register(@RequestBody User user,HttpSession session) {
        String code = user.getCode();  // 获取验证码
        // 验证码验证
        String sessionCode = (String) session.getAttribute("code");
        if (sessionCode == null || !sessionCode.equalsIgnoreCase(code)) {
            return ResponseDTO.error(-3, "验证码错误");
        }
        return userService.register(user);
    }
}