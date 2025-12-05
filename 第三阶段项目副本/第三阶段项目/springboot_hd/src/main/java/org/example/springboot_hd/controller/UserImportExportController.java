package org.example.springboot_hd.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.example.springboot_hd.dto.ResponseDTO;
import org.example.springboot_hd.entity.User;
import org.example.springboot_hd.service.UserImportExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @BelongsProject: springboot_hd
 * @BelongsPackage: org.example.springboot_hd.controller
 * @Author: 夏日花店
 * @CreateTime: 2025-11-02 13:30:00
 * @Description: 用户导入导出控制层 - 提供Excel和CSV格式的用户数据导入导出接口
 * @Version: 2.0 - Excel格式支持
 */
@RestController
@RequestMapping("/user")
public class UserImportExportController {

    @Autowired
    private UserImportExportService userImportExportService;

    /**
     * 导入用户数据（支持Excel和CSV格式）
     *
     * @param file 上传的Excel或CSV文件
     * @return ResponseDTO 导入结果，包含成功/失败统计和详细错误信息
     * @apiNote 支持批量导入用户数据，自动处理数据验证和密码加密
     * @example POST /user/import
     * Content-Type: multipart/form-data
     * Body: file=用户数据.xlsx
     */
    @PostMapping("/import")
    public ResponseDTO importUsers(@RequestParam("multipartFile") MultipartFile file) {
        return userImportExportService.importUsers(file);
    }

    /**
     * 导出用户数据到Excel文件
     *
     * @param user     查询条件（可选）
     * @param response HTTP响应对象
     * @return ResponseDTO 导出结果，包含文件信息和数据统计
     * @apiNote 支持按条件筛选导出用户数据，自动生成Excel文件下载
     * @example GET /user/export?userName=张&userGender=1
     * 浏览器自动下载"用户数据_时间戳.xlsx"文件
     */
    @GetMapping("/export")
    public void exportUsers(@ModelAttribute User user, HttpServletResponse response) {
        userImportExportService.exportUsers(user, response);  // 直接调用，不返回ResponseDTO
    }


    /**
     * 下载用户数据导入模板
     *
     * @param response HTTP响应对象
     * @return ResponseDTO 下载结果
     * @apiNote 提供标准化的Excel导入模板，包含字段说明和示例数据
     * @example
     * GET /user/downloadTemplate
     * 浏览器自动下载"用户导入模板.xlsx"文件
     */
    @GetMapping("/downloadTemplate")
    public void downloadTemplate(HttpServletResponse response) {
        userImportExportService.downloadTemplate(response);
    }
}