package org.example.springboot_hd.dto;

import lombok.Data;

/**
 * @BelongsProject: springboot_hd
 * @BelongsPackage: org.example.springboot_hd.dto
 * @Author: 夏日花店
 * @CreateTime: 2025-11-02 12:35:05
 * @Description: 头部注释
 * @Version: 1.0
 */
public class ExportDTO {
    /**
     * 导出请求DTO
     */
    @Data
    public static class ExportRequest {
        private String userName;
        private String userPhone;
        private String userCard;
        private String userGender;
        // 可以添加其他导出条件
    }

    /**
     * 导出响应DTO（如果需要返回导出结果信息）
     */
    @Data
    public static class ExportResponse  {
        private String fileName;
        private Integer totalCount;
        private String exportTime;
    }

    /**
     * 模板下载响应DTO
     */
    @Data
    public static class TemplateResponse {
        private String fileName;
        private String downloadUrl; // 如果有需要的话
        private String description;
    }
}
