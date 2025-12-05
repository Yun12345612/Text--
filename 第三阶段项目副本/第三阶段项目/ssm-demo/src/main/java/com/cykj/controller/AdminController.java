package com.cykj.controller;

import com.cykj.dto.ResponseDTO;
import com.cykj.pojo.CyAdmin;
import com.cykj.service.AdminService;
import com.cykj.vo.AdminStatusVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * @Author: 夏日花店
 * @CreateTime: 2025-10-14 11:17:25
 * @Description: 头部注释
 * @Version: 1.0
 */
@RestController
@RequestMapping("/admin")
public class AdminController {


    @Autowired
    private AdminService adminService;

    //1.登录
    @PostMapping("/login")
    public ResponseDTO login(@RequestBody CyAdmin cyAdmin, HttpSession session) {
        // 获取前端传入的验证码
        String inputCode = cyAdmin.getCode();
        // 从session中获取存储的验证码
        String sessionCode = (String) session.getAttribute("code");
        // 验证码验证
        if (sessionCode == null) {
            return new ResponseDTO(400, "验证码已过期，请刷新重试",null);
        }
        if (!sessionCode.equalsIgnoreCase(inputCode)) {
            return new ResponseDTO(400, "验证码错误",null);
        }
        // 验证成功后清除session中的验证码（一次性使用）
        session.removeAttribute("code");
        // 获取账号密码进行登录
        String acc = cyAdmin.getAdminAccount();
        String pwd = cyAdmin.getAdminPwd();
        return adminService.login(acc, pwd);
    }


    //2.更新管理员状态
    @RequestMapping("/updateAdminStatus")
    public ResponseDTO updateAdminStatus(@RequestBody AdminStatusVo vo) {
        return adminService.updateAdminStatus(vo.getIds());
    }


    //3.注册新管理员
    @PostMapping("/register")
    public ResponseDTO register(@RequestBody CyAdmin cyAdmin,HttpSession session) {
        String code = cyAdmin.getCode();  // 获取验证码

        // 验证码验证
        String sessionCode = (String) session.getAttribute("code");
        if (sessionCode == null || !sessionCode.equalsIgnoreCase(code)) {
            return ResponseDTO.error(-3, "验证码错误");
        }
        return adminService.register(cyAdmin);
    }
}
