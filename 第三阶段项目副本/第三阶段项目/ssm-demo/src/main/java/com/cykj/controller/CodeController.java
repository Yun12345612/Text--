package com.cykj.controller;

import com.cykj.utils.ImageCodeUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.net.ssl.HandshakeCompletedEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @Author: 夏日花店
 * @CreateTime: 2025-10-15 09:16:17
 * @Description: 头部注释
 * @Version: 1.0
 */
@RestController
@RequestMapping("/get")
public class CodeController {

    @RequestMapping("/code")
    public void getCode(HttpServletResponse response, HttpSession session) throws IOException {
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
        }
    }
}
