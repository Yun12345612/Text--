package org.example.springboot_hd.controller;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.springboot_hd.utils.ImageCodeUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @BelongsProject: springboot_hd
 * @BelongsPackage: org.example.springboot_hd.controller
 * @Author: 夏日花店
 * @CreateTime: 2025-10-28 11:45:36
 * @Description: 验证码控制层
 * @Version: 1.0
 */
@RestController
@RequestMapping("/get")
public class CodeController {

    @RequestMapping("/code")
    public void getCode(HttpServletResponse response, HttpSession session) throws IOException {
        // 设置响应类型为 PNG 图片（必须添加）
        response.setContentType("image/png");
        // 禁止缓存
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        // 创建验证码工具实例，设置宽度、高度、字符数、干扰线数
        ImageCodeUtils imageCodeUtils = new ImageCodeUtils(288, 58, 4, 20);

        // 将验证码存储到session中
        session.setAttribute("code", imageCodeUtils.getCode());
        System.out.println("sessionId:" + session.getId()+",存的验证码是"+session.getAttribute("code"));

        try {
            // 获取响应输出流并写入验证码图片
            ServletOutputStream os = response.getOutputStream();
            imageCodeUtils.write(os);
            os.flush();
            os.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
