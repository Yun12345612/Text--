package org.example.springboot_hd.service;

import jakarta.servlet.http.HttpServletResponse;
import org.example.springboot_hd.dto.ResponseDTO;
import org.example.springboot_hd.entity.User;
import org.springframework.web.multipart.MultipartFile;

public interface UserImportExportService {
    /**
     * 导入用户数据（支持Excel和CSV格式）
     */
    ResponseDTO importUsers(MultipartFile file);

    /**
     * 下载导入模板（Excel格式）
     */
    void downloadTemplate(HttpServletResponse response);  // 改为void

    /**
     * 导出用户数据（Excel格式）
     */
    void exportUsers(User user, HttpServletResponse response);  // 改为void
}