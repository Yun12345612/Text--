package org.example.springboot_hd.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.example.springboot_hd.dto.ResponseDTO;
import org.example.springboot_hd.entity.Admin;
import org.example.springboot_hd.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;


/**
 * @BelongsProject: springboot_hd
 * @BelongsPackage: org.example.springboot_hd.controller
 * @Author: 夏日花店
 * @CreateTime: 2025-10-28 09:04:45
 * @Description: 头部注释
 * @Version: 1.0
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // 全局设置响应编码（或在每个接口方法中设置）
    @InitBinder
    public void initBinder(WebDataBinder binder) {

    }

    //1.管理员登录
    @PostMapping("/login")
    public ResponseDTO login(HttpServletRequest request, @RequestBody Admin admin) {
        HttpSession session = request.getSession(); // 通过 request 获取 session
        // 获取前端传入的验证码
        String inputCode = admin.getCode();
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
        String account = admin.getAccount();
        String password = admin.getPassword();

        return adminService.login(account, password);
    }

    //2.管理员注册
    @PostMapping("/register")
    public ResponseDTO register(@RequestBody Admin admin, HttpSession session) {
        //获取验证码
        String code = admin.getCode();
        //验证码验证
        String sessionCode = (String) session.getAttribute("code");
        if (sessionCode == null || !sessionCode.equalsIgnoreCase(code)) {
            return ResponseDTO.error(-3, "验证码错误");
        }
        return adminService.registerAdmin(admin);
    }

    //2.管理员修改
    @PostMapping("/update")
    public ResponseDTO updateAdmin(@RequestBody Admin admin) {
        return adminService.updateAdmin(admin);
    }

    //3.管理员查询
    @GetMapping("/list")//ModelAttribute可以绑定多个参数
    public ResponseDTO listAdmin(@ModelAttribute Admin admin) {
        return adminService.selectAdminList(admin);
    }

    // 软删除管理员
    @DeleteMapping("/delete/{id}")//PathVariable从URL中动态获取参数
    public ResponseDTO delete(@PathVariable Long id) {
        return adminService.softDeleteAdmin(id);
    }

    //新增管理员
    @PostMapping("/add")
    public ResponseDTO addAdmin(@RequestBody Admin admin) {
        return adminService.addAdmin(admin);
    }
}
